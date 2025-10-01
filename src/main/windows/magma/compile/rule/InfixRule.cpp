// Generated transpiled C++ from 'src\main\java\magma\compile\rule\InfixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct InfixRule<>{Rule leftRule;, char* infix;, Rule rightRule;, Locator locator;};
template<>
Rule First_InfixRule(Rule left, char* infix, Rule right) {/*
		return new InfixRule(left, infix, right, new FirstLocator());
	*/}
template<>
Rule Last_InfixRule(Rule leftRule, char* infix, Rule rightRule) {/*
		return new InfixRule(leftRule, infix, rightRule, new LastLocator());
	*/}
template<>
/*CompileError>*/ lex_InfixRule(char* input) {/*
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
template<>
/*CompileError>*/ generate_InfixRule(Node node) {/*
		return leftRule.generate(node).flatMap(inner -> rightRule.generate(node).mapValue(other -> inner + infix + other));
	*/}
