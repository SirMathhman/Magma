struct NodeListRule(String key, Rule rule, Divider divider) implements Rule{};
Rule Statements_NodeListRule(String key, Rule rule, Divider divider) implements Rule(String key Rule rule) {}
Rule Delimited_NodeListRule(String key, Rule rule, Divider divider) implements Rule(String key Rule rule String delimiter) {}
Rule Values_NodeListRule(String key, Rule rule, Divider divider) implements Rule(String key Rule rule) {}
CompileError> lex_NodeListRule(String key, Rule rule, Divider divider) implements Rule(String input) {}
CompileError> generate_NodeListRule(String key, Rule rule, Divider divider) implements Rule(Node value) {}
