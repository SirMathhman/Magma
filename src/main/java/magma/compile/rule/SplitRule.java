package magma.compile.rule;

import magma.Tuple;
import magma.compile.Node;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Some;
import magma.result.Err;
import magma.result.Result;

public record SplitRule(Rule leftRule, Rule rightRule, Splitter splitter) implements Rule {

	public static Rule First(Rule left, String infix, Rule right) {
		final Splitter splitter = new InfixSplitter(infix, new FirstLocator()); return new SplitRule(left, right, splitter);
	}

	public static Rule Last(Rule leftRule, String infix, Rule rightRule) {
		final Splitter splitter = new InfixSplitter(infix, new LastLocator());
		return new SplitRule(leftRule, rightRule, splitter);
	}

	@Override
	public Result<Node, CompileError> lex(String input) {
		return switch (splitter.split(input)) {
			case None<Tuple<String, String>> _ ->
					new Err<>(new CompileError(splitter.createErrorMessage(), new StringContext(input)));
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
									 .flatMap(left -> rightRule.generate(node).mapValue(right -> splitter.merge(left, right)));
	}
}
