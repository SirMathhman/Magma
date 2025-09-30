package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Result;

public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
	@Override
	public Result<Node, CompileError> lex(String input) {
		final int index = input.indexOf(infix());
		if (index < 0) return new Err<>(new CompileError("Infix '" + infix + "' not present", new StringContext(input)));

		final String beforeContent = input.substring(0, index);
		final String content = input.substring(index + infix().length());

		return leftRule.lex(beforeContent).flatMap(left -> rightRule.lex(content).map(left::merge));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return leftRule.generate(node).flatMap(inner -> rightRule.generate(node).map(other -> inner + infix + other));
	}

}
