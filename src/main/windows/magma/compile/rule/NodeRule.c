struct NodeRule(String key, Rule rule) implements Rule{};
Rule Node_NodeRule(String key, Rule rule) implements Rule(char* key, Rule rule) {/*
		return new NodeRule(key, rule);
	*/}
/*CompileError>*/ lex_NodeRule(String key, Rule rule) implements Rule(char* content) {/*
		return rule.lex(content).mapValue(node -> new Node().withNode(key, node));
	*/}
/*CompileError>*/ generate_NodeRule(String key, Rule rule) implements Rule(Node node) {/*
		return switch (node.findNode(key)) {
			case None<Node> _ -> new Err<>(new CompileError("Node '" + key + "' not present", new NodeContext(node)));
			case Some<Node> v -> rule.generate(v.value());
		};
	*/}
