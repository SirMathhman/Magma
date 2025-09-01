Run `mvn -q -DskipTests=false clean test` before and after making changes to ensure tests fail and then pass.

Note: PMD/CPD thresholds in this repository are intentionally conservative to promote emergence
and small, expressive duplications rather than aggressive wholesale removal. When refactoring to
address CPD findings, prefer extracting small, well-named helpers and preserving emergent
patterns that improve readability and domain expressiveness rather than mechanically removing
every repeated block.
