package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Result;

import java.util.Optional;

public record InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule {
	public static Rule First(Rule left, String infix, Rule right) {
		return new InfixRule(left, infix, right, new FirstLocator());
	}

	public static Rule Last(Rule leftRule, String infix, Rule rightRule) {
		return new InfixRule(leftRule, infix, rightRule, new LastLocator());
	}

	@Override
	public Result<Node, CompileError> lex(String input) {
		final Optional<Integer> maybeIndex = locator.locate(input, input);
		if (maybeIndex.isEmpty())
			return new Err<>(new CompileError("Infix '" + infix + "' not present", new StringContext(input)));

		int index = maybeIndex.get();
		final String beforeContent = input.substring(0, index);
		final String content = input.substring(index + infix().length());

		return leftRule.lex(beforeContent).flatMap(left -> rightRule.lex(content).map(left::merge));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return leftRule.generate(node).flatMap(inner -> rightRule.generate(node).map(other -> inner + infix + other));
	}

}
