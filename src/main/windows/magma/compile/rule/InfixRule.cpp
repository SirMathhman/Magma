struct InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule{};
Rule First_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(Rule left, char* infix, Rule right) {/*
		return new InfixRule(left, infix, right, new FirstLocator());
	*/}
Rule Last_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(Rule leftRule, char* infix, Rule rightRule) {/*
		return new InfixRule(leftRule, infix, rightRule, new LastLocator());
	*/}
/*CompileError>*/ lex_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(char* input) {/*
		return switch (locator.locate(input, infix)) {
			case None<Integer> _ ->
					new Err<>(new CompileError("Infix '" + infix + "' not present", new StringContext(input)));
			case Some<Integer>(Integer index) -> {
				final String beforeContent = input.substring(0, index);
				final String content = input.substring(index + infix.length());

				yield leftRule.lex(beforeContent).flatMap(left -> rightRule.lex(content).mapValue(left::merge));
			}
		};
	*/}
/*CompileError>*/ generate_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(Node node) {/*
		return leftRule.generate(node).flatMap(inner -> rightRule.generate(node).mapValue(other -> inner + infix + other));
	*/}
