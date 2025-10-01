struct EmptyRule implements Rule{};
/*CompileError>*/ lex_EmptyRule implements Rule(char* content) {/*
		if (content.isEmpty()) return new Ok<>(new Node());
		return new Err<>(new CompileError("Content is not empty", new StringContext(content)));
	*/}
/*CompileError>*/ generate_EmptyRule implements Rule(Node node) {/*
		return new Ok<>("");
	*/}
