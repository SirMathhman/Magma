struct StringRule(String key) implements Rule{};
Rule String_StringRule(String key) implements Rule(char* key) {}
/*CompileError>*/ lex_StringRule(String key) implements Rule(char* content) {}
/*CompileError>*/ generate_StringRule(String key) implements Rule(Node node) {}
