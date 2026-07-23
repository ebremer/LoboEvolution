# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Git commits

- Author every commit as **Erich Bremer <erich@ebremer.com>** only.
- Do **not** add a `Co-Authored-By` trailer or any other co-author / attribution
  line to commit messages.

## Build & runtime notes

- Java 25. Build with `mvn -B install -DskipTests`. Note the `maven-clean-plugin`
  is intentionally skipped in the root `pom.xml`, so `mvn clean` is a no-op.
- The GraalJS/Truffle version (`LoboHTML/pom.xml` `<graaljs.version>`) must stay
  in lock-step with the GraalVM JDK it runs on. A patch-level mismatch throws
  `UnsatisfiedLinkError` (libgraal) the first time a script is evaluated and
  takes down the entire JS engine.
- Unit tests live in `LoboUnitTest`. The suite is mid Rhino→GraalJS migration and
  is partially red by design; `.github/workflows/test.yml` gates a green smoke
  subset (JS engine + SRI) and runs the full suite informationally.
