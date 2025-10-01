struct LazyRule{};
void set_LazyRule(Rule rule) {/*
		maybeChild = new Some<>(rule);
	*/}
/*CompileError>*/ lex_LazyRule(char* content) {/*
		return switch (maybeChild.map(child -> child.lex(content))) {
			case None<Result<Node, CompileError>> _ ->
					new Err<>(new CompileError("Child not set", new StringContext(content)));
			case Some<Result<Node, CompileError>> v -> v.value();
		};
	*/}
/*CompileError>*/ generate_LazyRule(Node node) {/*
		return switch (maybeChild.map(child -> child.generate(node))) {
			case None<Result<String, CompileError>> _ -> new Err<>(new CompileError("Child not set", new NodeContext(node)));
			case Some<Result<String, CompileError>> v -> v.value();
		};
	*/}
