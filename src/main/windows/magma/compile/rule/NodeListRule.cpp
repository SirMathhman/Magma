// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NodeListRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NodeListRule {/*String*/ key;/* Rule*/ rule;/* Divider*/ divider;};
/*public static Rule*/ Statements_NodeListRule(/*String*/ key, /* Rule*/ rule) {
	/*return new NodeListRule*/(/*key*/, /* rule*/, /* new FoldingDivider(new EscapingFolder(new StatementFolder())))*/;
}
/*public static Rule*/ Delimited_NodeListRule(/*String*/ key, /* Rule*/ rule, /* String*/ delimiter) {
	/*return new NodeListRule*/(/*key*/, /* rule*/, /* new DelimitedRule(delimiter))*/;
}
/*public static Rule*/ Arguments_NodeListRule(/*String*/ key, /* Rule*/ rule) {
	/*return new NodeListRule*/(/*key*/, /* rule*/, /* new FoldingDivider(new ValueFolder()))*/;
}
/*@Override
	public Result<Node, CompileError>*/ lex_NodeListRule(/*String*/ input) {
	/*return divider.divide*/(/*input)
									.reduce(new Ok<>(new ArrayList<>())*/, /* this::fold*/, /* (_*/, /* next) -> next)
									.mapValue(list -> new Node().withNodeList(key, list))*/;
}
/*private Result<List<Node>, CompileError>*/ fold_NodeListRule(/*Result<List<Node>, CompileError>*/ current, /* String*/ element) {
	/*return switch */(/*current) {
			case Err<List<Node>, CompileError> v -> new Err<>(v.error());
			case Ok<List<Node>*/, /* CompileError>(List<Node> list) -> switch (rule.lex(element)) {
				case Err<Node, CompileError> v -> new Err<>(v.error());
				case Ok<Node, CompileError>(Node node) -> {
					list.add(node);
					yield new Ok<>(list);
				}
			};
		}*/;
}
/*@Override
	public Result<String, CompileError>*/ generate_NodeListRule(/*Node*/ value) {
	/*Option<Result<String, CompileError>> resultOption */=/* value.findNodeList(key).map(list -> {
			// Treat missing or empty lists as empty content when generating.
			if (list.isEmpty()) return new Ok<>("");

			final StringJoiner sb = new StringJoiner(divider.delimiter());
			for (Node child : list)
				switch (this.rule.generate(child)) {
					case Ok<String, CompileError>(String value1) -> sb.add(value1);
					case Err<String, CompileError>(CompileError error) -> {
						return new Err<>(error);
					}
				}

			return new Ok<>(sb.toString());
		})*/;
	/*return switch */(/*resultOption) {
			// If the node-list isn't present at all*/, /* treat it as empty rather than an
			// error.
			case None<Result<String, CompileError>> _ -> new Ok<>("");
			case Some<Result<String, CompileError>>(Result<String*/, /* CompileError> value2) -> value2;
		}*/;
}
