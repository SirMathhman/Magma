## Error handling

This project avoids using checked exceptions for control flow. Instead, methods
return `Optional` or the custom `Result` type to represent failures explicitly.
This makes error cases clear in the type system and keeps method signatures easy
to read.
