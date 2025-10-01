struct NodeListRule(String key, Rule rule, Divider divider) implements Rule{};
Rule Statements_NodeListRule(String key, Rule rule, Divider divider) implements Rule(char* key, Rule rule) {/*
		return new NodeListRule(key, rule, new FoldingDivider(new EscapingFolder(new StatementFolder())));
	*/}
Rule Delimited_NodeListRule(String key, Rule rule, Divider divider) implements Rule(char* key, Rule rule, char* delimiter) {/*
		return new NodeListRule(key, rule, new DelimitedRule(delimiter));
	*/}
Rule Values_NodeListRule(String key, Rule rule, Divider divider) implements Rule(char* key, Rule rule) {/*
		return new NodeListRule(key, rule, new FoldingDivider(new ValueFolder()));
	*/}
/*CompileError>*/ lex_NodeListRule(String key, Rule rule, Divider divider) implements Rule(char* input) {/*
		final ArrayList<Node> children = new ArrayList<>(); for (String segment : divider.divide(input).toList()) {
			Result<Node, CompileError> res = rule().lex(segment);
			if (res instanceof Ok<Node, CompileError>(Node value)) children.add(value);
			else if (res instanceof Err<Node, CompileError>(CompileError error)) return new Err<>(error);
		}

		return new Ok<>(new Node().withNodeList(key, children));
	*/}
/*CompileError>*/ generate_NodeListRule(String key, Rule rule, Divider divider) implements Rule(Node value) {/*
		Option<Result<String, CompileError>> resultOption = value.findNodeList(key).map(list -> {
			final StringJoiner sb = new StringJoiner(divider.delimiter()); for (Node child : list)
				switch (this.rule.generate(child)) {
					case Ok<String, CompileError>(String value1) -> sb.add(value1);
					case Err<String, CompileError>(CompileError error) -> {
						return new Err<>(error);
					}
				}

			return new Ok<>(sb.toString());
		}); return switch (resultOption) {
			case None<Result<String, CompileError>> _ ->
					new Err<>(new CompileError("Node list '" + key + "' not present", new NodeContext(value)));
			case Some<Result<String, CompileError>>(Result<String, CompileError> value2) -> value2;
		};
	*/}
