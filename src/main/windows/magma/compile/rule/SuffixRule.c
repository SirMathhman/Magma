struct SuffixRule(Rule rule, String suffix) implements Rule{};
Rule Suffix_SuffixRule(Rule rule, String suffix) implements Rule(Rule rule, char* suffix) {}
/*CompileError>*/ lex_SuffixRule(Rule rule, String suffix) implements Rule(char* input) {}
/*CompileError>*/ generate_SuffixRule(Rule rule, String suffix) implements Rule(Node node) {}
Rule getRule_SuffixRule(Rule rule, String suffix) implements Rule() {}
