struct TagRule(String tag, Rule rule) implements Rule{};
Rule Tag_TagRule(String tag, Rule rule) implements Rule(char* type, Rule rule) {}
/*CompileError>*/ lex_TagRule(String tag, Rule rule) implements Rule(char* content) {}
/*CompileError>*/ generate_TagRule(String tag, Rule rule) implements Rule(Node node) {}
