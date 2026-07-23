# GraalJS migration — failure triage

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

### RC-A — DOM constructors are broken or missing 🔴
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

### RC-B — window methods aren't callable as bare globals 🔴
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

### RC-C — `new DOMParser().parseFromString(...)` NPEs 🟠
```
java.lang.NullPointerException: Cannot invoke
  "Document.getXmlEncoding()" because "this.doc" is null
```
The `DOMParserImpl` → `XMLDocument` path leaves `doc` null. **Fix:** initialise /
null-guard that path. **Hits:** part of the `xml` cluster.

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
