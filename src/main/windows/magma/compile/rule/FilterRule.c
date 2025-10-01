struct FilterRule implements Rule{};
public FilterRule_FilterRule implements Rule(Filter filter, Rule rule) {}
Rule Filter_FilterRule implements Rule(Filter filter, Rule rule) {}
Rule Identifier_FilterRule implements Rule(Rule rule) {}
/*CompileError>*/ lex_FilterRule implements Rule(char* content) {}
/*CompileError>*/ generate_FilterRule implements Rule(Node node) {}
