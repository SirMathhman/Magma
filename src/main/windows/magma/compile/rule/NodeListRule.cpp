// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NodeListRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>\nstruct NodeListRule<>{char* key;, Rule rule;, Divider divider;};
Rule Statements_NodeListRule(char* key, Rule rule) {/*
		return new NodeListRule(key, rule, new FoldingDivider(new EscapingFolder(new StatementFolder())));
	*/}
Rule Delimited_NodeListRule(char* key, Rule rule, char* delimiter) {/*
		return new NodeListRule(key, rule, new DelimitedRule(delimiter));
	*/}
Rule Values_NodeListRule(char* key, Rule rule) {/*
		return new NodeListRule(key, rule, new FoldingDivider(new ValueFolder()));
	*/}
/*CompileError>*/ lex_NodeListRule(char* input) {/*
		final ArrayList<Node> children = new ArrayList<>(); for (String segment : divider.divide(input).toList()) {
			Result<Node, CompileError> res = rule().lex(segment);
			if (res instanceof Ok<Node, CompileError>(Node value)) children.add(value);
			else if (res instanceof Err<Node, CompileError>(CompileError error)) return new Err<>(error);
		}

		return new Ok<>(new Node().withNodeList(key, children));
	*/}
/*CompileError>*/ generate_NodeListRule(Node value) {/*
		Option<Result<String, CompileError>> resultOption = value.findNodeList(key).map(list -> {
			// Treat missing or empty lists as empty content when generating.
			if (list.isEmpty()) return new Ok<>("");

			final StringJoiner sb = new StringJoiner(divider.delimiter()); for (Node child : list)
				switch (this.rule.generate(child)) {
					case Ok<String, CompileError>(String value1) -> sb.add(value1);
					case Err<String, CompileError>(CompileError error) -> {
						return new Err<>(error);
					}
				}

			return new Ok<>(sb.toString());
		});

		return switch (resultOption) {
			// If the node-list isn't present at all, treat it as empty rather than an
			// error.
			case None<Result<String, CompileError>> _ -> new Ok<>("");
			case Some<Result<String, CompileError>>(Result<String, CompileError> value2) -> value2;
		};
	*/}
