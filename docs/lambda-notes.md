# Lambda Notes

`ArrowHelper` rewrites Java lambdas using a straight text scan. Each line is
checked for the `->` token and it becomes `=>` in the output. This keeps the
conversion in a single pass without another parser. Typed parameters are
detected inside the parentheses and mapped to `name : type` using the
`TypeMapper` helper.

When an arrow body sits on one line `ArrowHelper` expands assignments using the
existing stubbing helpers. Multi-line bodies are also parsed so assignments
become stubs rather than being copied verbatim.

Recent tests exercise lambdas with parameters and multi-line blocks so that
future refactoring preserves this behaviour.

Lambda expressions can also appear inside method calls. The parser now keeps
these arguments intact by detecting the `->` token after the closing
parenthesis and skipping invocation stubbing in that case. This allows
`doThing(() -> 1)` to remain unchanged aside from the arrow replacement.
When the lambda spans multiple lines the stubber now parses each statement and
rewrites assignments so blocks like `fold(x, () -> {\n});` gain stubbed bodies.
