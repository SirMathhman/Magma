struct StringRule(String key) implements Rule{};
Rule String_StringRule(String key) implements Rule(String key) {}
CompileError> lex_StringRule(String key) implements Rule(String content) {}
CompileError> generate_StringRule(String key) implements Rule(Node node) {}
