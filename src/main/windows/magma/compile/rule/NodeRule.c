struct NodeRule(String key, Rule rule) implements Rule{};
Rule Node_NodeRule(String key, Rule rule) implements Rule(char* key, Rule rule) {}
/*CompileError>*/ lex_NodeRule(String key, Rule rule) implements Rule(char* content) {}
/*CompileError>*/ generate_NodeRule(String key, Rule rule) implements Rule(Node node) {}
