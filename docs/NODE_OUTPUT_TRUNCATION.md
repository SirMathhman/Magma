# Node Output Truncation

We capped `Node#format`/`toString` to three structural levels so debug dumps stay readable when trees grow deep. Nested `Node` children or lists beyond that depth now render as `{...}` or `[...]` with an ellipsis, signaling trimmed content without flooding logs. No behavior changes occur in the underlying tree, only in how it is printed. To verify, run `mvn -Dtest=DeserializationDebugTest test` and inspect the logged node dumps to see the ellipsis appear past the third level.
