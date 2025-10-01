struct EmptyRule implements Rule{};
/*CompileError>*/ lex_EmptyRule implements Rule(char* content) {}
/*CompileError>*/ generate_EmptyRule implements Rule(Node node) {}
