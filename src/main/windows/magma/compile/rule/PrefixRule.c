struct PrefixRule(String prefix, Rule rule) implements Rule{};
Rule Prefix_PrefixRule(String prefix, Rule rule) implements Rule(char* prefix, Rule rule) {}
/*CompileError>*/ lex_PrefixRule(String prefix, Rule rule) implements Rule(char* content) {}
/*CompileError>*/ generate_PrefixRule(String prefix, Rule rule) implements Rule(Node node) {}
