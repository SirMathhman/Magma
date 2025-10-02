package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Some;
import magma.result.Err;
import magma.result.Result;
import magma.Tuple;

public record SplitRule(Rule leftRule, Rule rightRule, Splitter splitter, String errorMessage, String separator)
		implements Rule {

	public static Rule First(Rule left, String infix, Rule right) {
		final Splitter splitter = new InfixSplitter(infix, new FirstLocator());
		return new SplitRule(left, right, splitter, "Infix '" + infix + "' not present", infix);
	}

	public static Rule Last(Rule leftRule, String infix, Rule rightRule) {
		final Splitter splitter = new InfixSplitter(infix, new LastLocator());
		return new SplitRule(leftRule, rightRule, splitter, "Infix '" + infix + "' not present", infix);
	}

	@Override
	public Result<Node, CompileError> lex(String input) {
		return switch (splitter.split(input)) {
			case None<Tuple<String, String>> _ ->
				new Err<>(new CompileError(errorMessage, new StringContext(input)));
			case Some<Tuple<String, String>>(Tuple<String, String> parts) -> {
				final String left = parts.left();
				final String right = parts.right();
				yield leftRule.lex(left).flatMap(leftNode -> rightRule.lex(right).mapValue(leftNode::merge));
			}
		};
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return leftRule.generate(node)
				.flatMap(left -> rightRule.generate(node).mapValue(right -> left + separator + right));
	}
}
