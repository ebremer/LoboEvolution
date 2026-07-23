# GraalJS migration — failure triage

> ## ▶ RESUME HERE (handoff, 2026-07-23)
>
> **State:** branch `develop`, tree clean, nothing pushed. Latest full-suite
> baseline: **5,690 run · 2,530 failures · 8 errors · 1 skipped** (GraalVM JDK 25).
> Committed: the whole review backlog (C1–C2, H1–H2, M1–M5, L1–L6), a 5-item
> hygiene batch, the three systemic bridge fixes **RC-A/B/C** (2,644 → 2,537),
> and the **CSSOM `CssMembers` rework** (2,537 → 2,530, −7, zero regressions;
> commit `828b36b69`). See `git log` for per-item commits.
>
> **CssMembers rework — DONE (2026-07-23).** Made inline `element.style` member
> access CSSOM-correct: recognized property unset → `""` (not null); unrecognized
> name → `undefined` via `hasMember`=false (not null, even if present in the store
> from inline parsing); numeric key → `item(n)`; read-only `length` write throws;
> `x=null` clears to `""` ([LegacyNullToEmptyString]). Known-property gate =
> `org.htmlunit.cssparser.util.CSSProperties` registry ∪ bean getters. Guarded by
> `GraalCssMembersTest` (in the smoke gate) + updated `GraalCssStyleProxyTest`.
> **Lesson learned:** the rework is browser-correct but the raw win is only −7,
> because ~20 tests' `@Alerts` asserted a browser-*incorrect* `"null"` for an
> unset inline property (green-by-accident against the old null-returning bridge);
> those 20 were corrected to real browser values (`""`) in the same commit, so the
> net is −7 with **zero regressions**. The unregistered-JS-write expando (e.g.
> `style.pixelLeft=123` reading back) is NOT supported — `HTMLElementImpl.getStyle`
> re-creates the wrapper while the declaration is empty, so a per-object expando
> can't persist; such reads stay `undefined` (a handful of tests, no regressions).
>
> **Next task:** the **Select/Option/Form cluster** is now the biggest single
> lever (~285 failing across `HTMLSelectElementTest`/`HTMLOptionsCollectionTest`/
> `HTMLOptionElement2Test`/`HTMLFormElementTest`), and unlike the CSS residual it
> has a **confirmed systemic root cause** — see the *Select / Option / Form
> cluster* section below. Two bridge bugs: (1) named/indexed access on collection
> objects (`document.forms.testForm`, `form.select1` → `undefined`) — make
> `HTMLCollection`/`HTMLFormElement` `ProxyObject`s like `CssMembers`; (2)
> `HTMLFormElementImpl.getElements()` returns wrong wrappers (`form.elements[0]`
> has `length 1`, no `.options`). The select+options work in isolation, so it's a
> bridge bug, not options parsing. Do each focused and re-measure.
>
> After that, the CSS residual is **per-value/per-feature**, not one bug (this
> rework netted only −7): shorthand→longhand expansion (`style.length`, e.g.
> `expected <4> but was <2>`), computed-style value correctness (layout-dependent,
> `expected <0px> but was <784px>`), and the separate ~315 `domts.*` DOM-conformance
> track (drives the Java DOM API directly, no JS bridge). Cluster by feature,
> measure by diffing the failing *set*, and beware green-by-accident.
>
> **How to work here (established conventions):**
> - Commits: author **Erich Bremer <erich@ebremer.com>** only, **no `Co-Authored-By`
>   trailer** (see `CLAUDE.md`). One commit per fix.
> - Build: `mvn -B install -DskipTests -Dmaven.javadoc.skip=true`. Tests:
>   `mvn -B test -pl LoboUnitTest`. GraalJS/Truffle version lives in
>   `parent/pom.xml` `<graaljs.version>` and MUST match the GraalVM JDK.
> - **Measure every fix** by diffing the *set* of failing tests (not just the
>   count) against the prior full-suite log — a matching count can hide pass↔fail
>   swaps. Extract with:
>   `grep -E '^\[ERROR\]   [A-Za-z]' log | sed 's/^\[ERROR\]   //' | sed 's/[ :»].*//' | sort -u`
>   then `comm -13 old new` (regressions) / `comm -23 old new` (wins).
> - Many "regressions" are **green-by-accident** tests that expected an exception
>   only because something was broken — verify each before treating it as real.
> - Reproduce causes against `target/loboevolution-5.0.jar` with a tiny harness:
>   build an `HTMLDocumentImpl` (see `LoboWebDriver.loadHtml`) + `GraalJsEngine` +
>   `JsEngineFactory.bindWindowGlobals`, then `engine.eval(...)`. Faster than the
>   full suite for pinpointing behavior.
> - Add a guard test in `LoboUnitTest/.../js/engine/` for each fix and append it to
>   the **blocking smoke list** in `.github/workflows/test.yml`.
> - **Dead end (do not retry):** making `__noSuchProperty__` throw `ReferenceError`
>   for unknown names breaks `typeof undeclaredVar` (must stay `"undefined"`).

Snapshot: **2026-07-23**, against the full `LoboUnitTest` suite
(**5,681 run · 2,644 failures · 8 errors · 1 skipped**, run on GraalVM JDK 25).

The goal of this pass was to find out whether the 2,644 failures are ~2,644
independent bugs or a handful of shared root causes. **Answer: a mix, but there
are at least three systemic bridge bugs that each break many tests, and one
large family of failures that has nothing to do with the JS bridge at all.**

## Method

The unit tests use an alert-comparison harness (`LoboUnitTest.checkHtmlAlert` +
`AlertsExtension`): each test loads an HTML fragment whose `<script>` calls
`alert(...)` once per value it wants to report, and the captured
`window.getMsg()` list is compared to the `@Alerts(...)` annotation. So:

- **`actual array was <null>`** → the script threw *before any* `alert` (uncaught).
- **`array lengths differ`** → the script ran partway then threw.
- **`array contents differ` / `expected: <x> but was: <y>`** → the script ran but
  produced wrong values.

Failure-message distribution (normalized):

| count | shape | meaning |
|------:|-------|---------|
| 1806 | `expected: <x> but was: <y>` | ran, wrong value |
| 860 | `array contents differ at [i]` | ran, wrong element |
| ~679 | `actual array was <null>` | **script threw uncaught** |
| 624 | `array lengths differ` | script threw partway |
| ~315 | `Xxx##Assert## ==> …` | W3C DOM conformance (see family B) |

Root causes were then confirmed by reproducing them directly against the built
uber jar (a small `loadHtml` + `window.getMsg()` harness), not inferred.

## Two families of failure

Distinct failing test methods by package:

| package | failing | family |
|---------|--------:|--------|
| `html` | 1006 | A — JS bridge |
| `css` (+`css.property`) | 421 | A — JS bridge |
| `dom` | 364 | A — JS bridge |
| `event` | 280 | A — JS bridge |
| `xml` | 116 | A — JS bridge |
| `canvas` | 77 | A — JS bridge |
| `junit`/`worker`/`wpt` | 73 | A — JS bridge |
| `domts.level1/2/3` | ~315 | **B — DOM conformance** |

- **Family A (~2,320): JS-bridge tests.** Run script, compare alerts. Affected by
  the systemic bridge bugs below *and* by genuine per-feature value differences.
- **Family B (~315): W3C DOM Test Suite** (`domts.level1/2/3`). These drive the
  Java DOM API **directly** — no JavaScript, no bridge. They are a *separate*
  root-cause family (DOM-implementation conformance gaps, e.g. `normalizeDocument`,
  `renameNode`, `getSchemaTypeInfo`, `compareDocumentPosition`) and several likely
  predate the migration. Do not lump these in with the bridge work.

## Confirmed systemic bridge bugs (fix once, clear many)

### RC-A — DOM constructors are broken or missing 🔴 — FIXED (2026-07-23)
> Bound the event impls + `Option`/`Image` as `ProxyInstantiable` in
> `JsEngineFactory`. **Suite: 2,644 → 2,558 failures (−86): 114 tests newly pass,
> 28 newly fail.** The 28 were green *by accident* — they expected an exception and
> only got one because construction was broken (e.g. `createCtorUnknownType` passes
> an *undefined variable* as the type, which should be a `ReferenceError`; with
> construction fixed the missing ReferenceError is exposed). Those belong to RC-B
> (bare-identifier scope) and per-feature edge-case validation, not to this fix.
> Guarded by `GraalDomConstructorsTest` (in the blocking smoke gate).


```js
new Event('click')   // TypeError: no applicable overload found
new Option('t','v')  // TypeError: undefined is not a function
new Image()          // TypeError: undefined is not a function
```
`JsEngineFactory.bindDomTypes` binds `Event`/`MouseEvent`/`KeyboardEvent`/
`CustomEvent` as **raw `.class`** objects. GraalJS then matches `new Event('click')`
against the impl's real constructors — `EventImpl()`, `EventImpl(Object[])`,
`EventImpl(InputEvent)` — and none accept a single `String`, so construction
throws. `Option`/`Image` (and other constructors the DOM defines) aren't bound at
all → `undefined is not a function`.
*Note:* the **old-style** path works — `document.createEvent('Event')` +
`initEvent` + `dispatchEvent` + `addEventListener` all behave correctly. Only the
`new X(...)` constructor form is broken.
**Fix:** bind these like `XMLHttpRequest`/`FormData` already are — a
`ProxyInstantiable` that constructs the impl from the JS args with the spec
`(type, init)` semantics — and add the missing ones (`Option`, `Image`, …).
**Hits:** the `event` cluster and every Select/Option/Image test that constructs
via `new`.

### RC-B — window methods aren't callable as bare globals 🔴 — FIXED (2026-07-23)
> `__noSuchProperty__` now falls through to `window[name]`, so bare
> `getComputedStyle`/`matchMedia`/`getSelection`/`atob`/`btoa` are callable.
> **Suite: 2,558 → 2,546 (−12), no real regressions** (one net-zero PopState swap,
> again a green-by-accident unmask). The win is small only because ~119 tests call
> `window.getComputedStyle` (property access, which already worked) and still fail
> on computed-*value* correctness — a per-feature CSS gap, not callability.
> **Dead end noted:** making `__noSuchProperty__` throw `ReferenceError` for
> unknown names (to recover RC-A's `createCtorUnknownType` cases) is wrong — the
> hook fires during `typeof`, so it made `typeof undeclaredVar` throw instead of
> yielding `"undefined"`. Reverted; guarded by `GraalGlobalFunctionsTest`.


```js
getComputedStyle(el)  // TypeError: undefined is not a function
```
Only a handful of functions (`alert`, `setTimeout`, `setInterval`, `clearTimeout`,
`clearInterval`) are re-exported as bare globals by `WINDOW_FUNCTION_BRIDGE`. Real
browsers make *every* `window` member reachable without the `window.` prefix;
here `getComputedStyle`, `matchMedia`, `getSelection`, `atob`/`btoa`, `scrollTo`,
etc. are not. `getComputedStyle` alone is used pervasively across the CSS tests.
**Fix:** make bare identifier lookups fall through to `window` members — extend the
`__noSuchProperty__` hook (it currently only does `getElementById`) to also return
`window[name]`, or install `window` as the global prototype.
**Hits:** a large share of the `css` cluster and any test calling a bare window
function.

### RC-C — `new DOMParser().parseFromString(...)` NPEs 🟠 — FIXED (2026-07-23)
> `parseFromString` mangled the source (`replace("<","><").replace(">>",">")`),
> which corrupted valid XML (`<a>1</a>` -> `<a>1></a>`); the SAX parse then failed,
> left `XMLDocument.doc` null, and `getXML()` NPE'd. Parse the source verbatim and
> null-guard `getXmlEncoding()`. **Suite: 2,546 → 2,537 (−9), zero regressions.**
> Guarded by `GraalDomConstructorsTest.domParserParsesXml`.


```
java.lang.NullPointerException: Cannot invoke
  "Document.getXmlEncoding()" because "this.doc" is null
```
The `DOMParserImpl` → `XMLDocument` path leaves `doc` null. **Fix:** initialise /
null-guard that path. **Hits:** part of the `xml` cluster.

## CSS cluster sub-triage (2026-07-23) — ✅ CssMembers rework DONE (commit 828b36b69)

> The `CssMembers` rework described below is **implemented**. The `undefined`/`""`/
> numeric-index/read-only semantics are correct and guarded. Shorthand expansion
> and computed-style values remain open (separate, larger sub-tracks — see the
> RESUME HERE block). What follows is the original analysis, kept for context.

With RC-A/B/C done, `CSSStyleDeclarationTest` is the biggest single class (128/163
failing). It is **not** one bug — the mismatch shapes spread out — but two share a
locus in the CSS member proxy (`CssMembers`) and a registry now exists to fix them
correctly:

Top mismatch shapes (from the failing run):
- `expected <undefined> but was <null>` ×14 — reading a non-CSS name yields Java null.
- `expected <string> but was <object>` ×6 — `typeof style.unsetProp` is "object"
  (Java null → JS null) instead of "string" ("").
- count too low, e.g. `expected <4> but was <2>` ×6, `<11> but was <2>` — shorthand
  properties don't expand into their longhands (`style.length`, enumeration).
- `expected <0px> but was <784px>` — layout-dependent computed values.

Confirmed against the built jar:
- `style.item(0)` → "color" ✓ but **`style[0]` → null** (numeric index access not
  routed to `item(n)`).
- **`style.margin` (unset) → null** (typeof "object"), should be "" — because
  `CssMembers.getMember` is reflection-first and returns the bean getter's null
  without falling through to `getPropertyValue`.

**Correct fix is not a plain null→"" swap.** CSSOM distinguishes: a *recognized* CSS
property reads as its value or "" (never null); an *unrecognized* name reads as
`undefined`. So `CssMembers` needs: (a) getter-returns-null → fall through to
`getPropertyValue` for known properties; (b) numeric keys → `item(n)`; (c)
`hasMember` return false for unknown non-property names so they read as `undefined`
rather than "". Use `org.htmlunit.cssparser.util.CSSProperties` as the known-property
registry. This is a core-path change (CSS access is everywhere) — do it focused and
re-measure, not as a drive-by. Shorthand expansion and computed-value correctness are
separate, larger sub-tracks.

## Select / Option / Form cluster (2026-07-23) — the next systemic lever

After the CssMembers rework, the single biggest failing-test cluster is
Select/Option/Form: `HTMLSelectElementTest` (123), `HTMLOptionsCollectionTest`
(91), `HTMLOptionElement2Test` (30), `HTMLFormElementTest` (41), plus form-driven
tests elsewhere. The dominant shape is **`expected <N> but was <1>`** (the actual
count is `1` for every N) and `array lengths differ`. Reproduced through the real
harness (`loadHtml` + `onload`) with a 3-option select in a `<form name=testForm>`:

- `document.getElementById('sid').length` → **3**, `.options.length` → **3** ✓
  (the select itself and its options collection are correct — see also the
  standalone probe: `getOptions().getLength()` = 3).
- `document.forms[0]` (indexed) → the form ✓; but **`document.forms.testForm`
  (named) → `undefined`** and **`form.select1` (named) → `undefined`**. Raw
  `HTMLCollectionImpl` / `HTMLFormElementImpl` are not `ProxyObject`s, so a
  name key falls through GraalJS bean reflection to `undefined` (there is a
  `namedItem`, but nothing routes `coll.name` to it). This is the CssMembers
  problem again, one class over.
- **`form.elements` is wrong at the source:** `form.elements.length` → **1** and
  `form.elements[0].length` → **1** with **no `.options`** — i.e. it does not
  return the real `HTMLSelectElementImpl`. `HTMLFormElementImpl.getElements()`
  builds its list by scanning *every* node in the document and keeping those with
  *any attribute value equal to the form's name*, plus an ad-hoc `findChild`
  recursion (see lines ~95-125). That heuristic is broken; it should return the
  form's descendant/associated controls (input/select/textarea/button/...).

So the cluster has **two systemic root causes**, both needed to clear the
`document.forms.testForm.select1`-style tests: (a) named/indexed access on
collection-like bridge objects (`HTMLCollection`, and `HTMLFormElement`'s named
controls) — make them `ProxyObject`s routing numeric→`item(n)` and name→named
lookup, mirroring `CssMembers`; (b) rewrite `HTMLFormElementImpl.getElements()`
to gather real form controls. Regression-risky (collection access is everywhere)
— do each focused and re-measure by diffing the failing *set*. Note the select
and its options already work in isolation, so this is a bridge/collection bug,
**not** an options-parsing bug.

## What is *not* systemic

`classList`, `element.style` get/set, `getElementsByTagName`/`querySelector[All]`
+ `.length`/`[i]`, `getAttribute`/`setAttribute`, `select.options`, `children`,
`Array.from(nodeList)`, `JSON`, `location`, `history`, `createEvent`/`dispatch`
all work. So the ~1,800 `expected/was` value-mismatch failures are largely
**genuine per-feature differences** (computed-style values, serialization details,
attribute defaults) — real migration work, not one bug.

## Recommended order

1. Fix **RC-A**, re-run the suite, measure the drop. (Constructors are self-contained.)
2. Fix **RC-B**, re-run, measure. (Bare-global fall-through.)
3. Fix **RC-C**.
4. Re-triage the residual Family-A `expected/was` failures — now dominated by
   genuine per-feature work — cluster by feature (CSSOM, select/option, canvas).
5. Treat **Family B** (`domts`) as a separate DOM-conformance track.
6. Only after RC-A/B/C: `@Disabled`-tag the true residual and flip the CI
   full-suite job to blocking (roadmap item #3).

Fixing RC-A/B/C first is what makes the "2,644" number meaningful — it converts a
pile of "script threw" noise into a measurable per-feature backlog.

---
*Reproductions were run against `target/loboevolution-5.0.jar`; message counts
come from the full-suite log. Numbers are a point-in-time snapshot — re-run the
suite to refresh.*
