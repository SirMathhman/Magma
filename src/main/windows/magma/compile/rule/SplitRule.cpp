// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SplitRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct SplitRule{Rule leftRule;, Rule rightRule;, Splitter splitter;};
template<>
Rule First_SplitRule(Rule left, char* infix, Rule right) {/*
		final Splitter splitter = new InfixSplitter(infix, new FirstLocator()); return new SplitRule(left, right, splitter);
	*/}
template<>
Rule Last_SplitRule(Rule leftRule, char* infix, Rule rightRule) {/*
		final Splitter splitter = new InfixSplitter(infix, new LastLocator());
		return new SplitRule(leftRule, rightRule, splitter);
	*/}
template<>
Rule Split_SplitRule(Rule left, Splitter splitter, Rule right) {/*
		return new SplitRule(left, right, splitter);
	*/}
template<>
Result<Node, CompileError> lex_SplitRule(char* input) {/*
		return switch (splitter.split(input)) {
			case None<Tuple<String, String>> _ ->
					new Err<>(new CompileError(splitter.createErrorMessage(), new StringContext(input)));
			case Some<Tuple<String, String>>(Tuple<String, String> parts) -> {
				final String left = parts.left();
				final String right = parts.right();
				yield leftRule.lex(left).flatMap(leftNode -> rightRule.lex(right).mapValue(leftNode::merge));
			}
		};
	*/}
template<>
Result<String, CompileError> generate_SplitRule(Node node) {/*
		return leftRule.generate(node)
									 .flatMap(left -> rightRule.generate(node).mapValue(right -> splitter.merge(left, right)));
	*/}
