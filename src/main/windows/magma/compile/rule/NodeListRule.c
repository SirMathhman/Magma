struct NodeListRule(String key, Rule rule, Divider divider) implements Rule{};
Rule Statements_NodeListRule(String key, Rule rule, Divider divider) implements Rule(char* key, Rule rule) {}
Rule Delimited_NodeListRule(String key, Rule rule, Divider divider) implements Rule(char* key, Rule rule, char* delimiter) {}
Rule Values_NodeListRule(String key, Rule rule, Divider divider) implements Rule(char* key, Rule rule) {}
/*CompileError>*/ lex_NodeListRule(String key, Rule rule, Divider divider) implements Rule(char* input) {}
/*CompileError>*/ generate_NodeListRule(String key, Rule rule, Divider divider) implements Rule(Node value) {}
