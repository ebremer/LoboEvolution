# Unimplemented method stubs

Many DOM/SVG interface methods are still **stubs** that return a placeholder
(`null`, `0`, `false`, empty) rather than doing anything. They are marked with a
consistent comment so they can be found and are not mistaken for working
behaviour:

```java
// TODO Auto-generated method stub
```

Because a stub silently returns a plausible-looking value, a caller (or a web
page's script) can't tell "not implemented" apart from a real result — hence
this inventory. Prefer implementing the real behaviour; if a method genuinely
cannot be supported yet, consider throwing/logging instead of returning a
misleading value.

## How to regenerate this list

This file is a **dated snapshot**, not a live view. Refresh it with:

```bash
# total
grep -rn "Auto-generated method stub" --include=*.java --exclude-dir=LoboUnitTest .

# per file, most first
grep -rl "Auto-generated method stub" --include=*.java --exclude-dir=LoboUnitTest . \
  | while read f; do echo "$(grep -c 'Auto-generated method stub' "$f") $f"; done | sort -rn
```

## Snapshot — 2026-07-22

**322 stubs** across **43 files**, all in three modules:

| Module | Stubs |
|--------|------:|
| LoboHTML | 268 |
| LoboSVG | 53 |
| LoboJhrome | 1 |

### By file (most first)

| Stubs | File |
|------:|------|
| 33 | `LoboHTML/.../html/dom/nodeimpl/DocumentImpl.java` |
| 33 | `LoboHTML/.../html/dom/domimpl/HTMLInputElementImpl.java` |
| 30 | `LoboHTML/.../html/js/WindowImpl.java` |
| 22 | `LoboHTML/.../html/js/ConsoleImpl.java` |
| 20 | `LoboHTML/.../html/dom/domimpl/HTMLButtonElementImpl.java` |
| 18 | `LoboHTML/.../html/dom/domimpl/HTMLImageElementImpl.java` |
| 16 | `LoboSVG/.../svg/dom/SVGSVGElementImpl.java` |
| 16 | `LoboHTML/.../html/dom/domimpl/HTMLBasicInputElement.java` |
| 12 | `LoboHTML/.../html/dom/nodeimpl/ElementImpl.java` |
| 11 | `LoboHTML/.../html/js/LocationImpl.java` |
| 10 | `LoboHTML/.../html/dom/domimpl/HTMLElementImpl.java` |
| 9 | `LoboSVG/.../svg/smil/TimeImpl.java` |
| 9 | `LoboHTML/.../html/dom/domimpl/HTMLScriptElementImpl.java` |
| 8 | `LoboSVG/.../svg/dom/SVGTextElementImpl.java` |
| 7 | `LoboHTML/.../html/js/NavigatorImpl.java` |
| 7 | `LoboHTML/.../html/js/HistoryImpl.java` |
| 6 | `LoboHTML/.../html/dom/domimpl/HTMLProcessingInstruction.java` |
| 6 | `LoboHTML/.../html/dom/domimpl/HTMLFormElementImpl.java` |
| 4 | `LoboHTML/.../html/dom/domimpl/HTMLTableColElementImpl.java` |
| 3 | `LoboSVG/.../svg/dom/SVGPathElementImpl.java` |
| 3 | `LoboSVG/.../svg/dom/SVGLocatableImpl.java` |
| 3 | `LoboSVG/.../svg/dom/SVGGradientElementImpl.java` |
| 3 | `LoboHTML/.../html/dom/domimpl/HTMLTableSectionElementImpl.java` |
| 3 | `LoboHTML/.../html/dom/domimpl/HTMLOListElementImpl.java` |
| 3 | `LoboHTML/.../html/dom/domimpl/HTMLCanvasElementImpl.java` |
| 2 | `LoboSVG/.../svg/dom/SVGUseElementImpl.java` |
| 2 | `LoboSVG/.../svg/dom/SVGSymbolElementImpl.java` |
| 2 | `LoboSVG/.../svg/dom/SVGElementImpl.java` |
| 2 | `LoboSVG/.../svg/dom/SVGAnimationImpl.java` |
| 2 | `LoboHTML/.../html/renderer/TranslatedRenderable.java` |
| 2 | `LoboHTML/.../html/dom/input/BasicInput.java` |
| 2 | `LoboHTML/.../html/dom/domimpl/HTMLTextAreaElementImpl.java` |
| 2 | `LoboHTML/.../html/dom/domimpl/HTMLHRElementImpl.java` |
| 2 | `LoboHTML/.../html/dom/domimpl/HTMLBodyElementImpl.java` |
| 1 | `LoboSVG/.../svg/dom/SVGTransformImpl.java` |
| 1 | `LoboSVG/.../svg/dom/SVGImageElementImpl.java` |
| 1 | `LoboSVG/.../svg/dom/SVGAnimationElementImpl.java` |
| 1 | `LoboJhrome/.../tabs/jhrome/JhromeContentPanelBorder.java` |
| 1 | `LoboHTML/.../html/renderer/RLine.java` |
| 1 | `LoboHTML/.../html/dom/nodeimpl/TextImpl.java` |
| 1 | `LoboHTML/.../html/dom/domimpl/HTMLTableRowElementImpl.java` |
| 1 | `LoboHTML/.../html/dom/domimpl/HTMLSelectElementImpl.java` |
| 1 | `LoboHTML/.../html/dom/domimpl/DOMTokenListImpl.java` |
