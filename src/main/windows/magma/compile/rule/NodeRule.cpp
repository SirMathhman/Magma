// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NodeRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NodeRule{char* key;Rule rule;};
Rule Node_NodeRule(char* key, Rule rule) {/*
		return new NodeRule(key, rule);*/}
Result<Node, CompileError> lex_NodeRule(char* content) {/*
		return rule.lex(content).mapValue(node -> new Node().withNode(key, node));*/}
Result<String, CompileError> generate_NodeRule(Node node) {/*
		return switch (node.findNode(key)) {
			case None<Node> _ -> new Err<>(new CompileError("Node '" + key + "' not present", new NodeContext(node)));
			case Some<Node> v -> rule.generate(v.value());
		}*//*;*/}
