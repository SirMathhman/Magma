struct PlaceholderRule(Rule rule) implements Rule{};
char* wrap_PlaceholderRule(Rule rule) implements Rule(char* input) {}
Rule Placeholder_PlaceholderRule(Rule rule) implements Rule(Rule rule) {}
/*CompileError>*/ lex_PlaceholderRule(Rule rule) implements Rule(char* content) {}
/*CompileError>*/ generate_PlaceholderRule(Rule rule) implements Rule(Node node) {}
