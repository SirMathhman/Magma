// Generated transpiled C++ from 'src\main\java\magma\compile\rule\EmptyRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>\nstruct EmptyRule<>{new EmptyRule();};
/*CompileError>*/ lex_EmptyRule(char* content) {/*
		if (content.isEmpty()) return new Ok<>(new Node());
		return new Err<>(new CompileError("Content is not empty", new StringContext(content)));
	*/}
/*CompileError>*/ generate_EmptyRule(Node node) {/*
		return new Ok<>("");
	*/}
