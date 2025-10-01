struct PrefixRule(String prefix, Rule rule) implements Rule{};
Rule Prefix_PrefixRule(String prefix, Rule rule) implements Rule(String prefix,  Rule rule) {}
CompileError> lex_PrefixRule(String prefix, Rule rule) implements Rule(String content) {}
CompileError> generate_PrefixRule(String prefix, Rule rule) implements Rule(Node node) {}
