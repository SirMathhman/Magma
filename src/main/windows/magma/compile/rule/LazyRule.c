struct LazyRule implements Rule{};
void set_LazyRule implements Rule(Rule rule) {}
/*CompileError>*/ lex_LazyRule implements Rule(char* content) {}
/*CompileError>*/ generate_LazyRule implements Rule(Node node) {}
