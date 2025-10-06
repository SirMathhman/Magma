package magma.compile.rule;

import magma.Tuple;
import magma.compile.Node;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Some;
import magma.result.Err;
import magma.result.Result;

public record SplitRule(Rule leftRule, Rule rightRule, Splitter splitter, Order order) implements Rule {
	public sealed interface Order {
		Result<Node, CompileError> evaluate(String left, String right, Rule leftRule, Rule rightRule);
	}

	public static final class LeftFirst implements Order {
		@Override
		public Result<Node, CompileError> evaluate(String left, String right, Rule leftRule, Rule rightRule) {
			return leftRule.lex(left).flatMap(leftNode -> rightRule.lex(right).mapValue(leftNode::merge));
		}
	}

	public static final class RightFirst implements Order {
		@Override
		public Result<Node, CompileError> evaluate(String left, String right, Rule leftRule, Rule rightRule) {
			return rightRule.lex(right).flatMap(rightNode -> leftRule.lex(left).mapValue(rightNode::merge));
		}
	}

	public static Rule First(Rule left, String infix, Rule right) {
		final Splitter splitter = new InfixSplitter(infix, new FirstLocator());
		return new SplitRule(left, right, splitter, new LeftFirst());
	}

	public static Rule Last(Rule leftRule, String infix, Rule rightRule) {
		final Splitter splitter = new InfixSplitter(infix, new LastLocator());
		return new SplitRule(leftRule, rightRule, splitter, new LeftFirst());
	}

	public static Rule Split(Rule left, Splitter splitter, Rule right) {
		return new SplitRule(left, right, splitter, new LeftFirst());
	}

	@Override
	public Result<Node, CompileError> lex(String input) {
		return switch (splitter.split(input)) {
			case None<Tuple<String, String>> _ ->
					new Err<>(new CompileError(splitter.createErrorMessage(), new StringContext(input)));
			case Some<Tuple<String, String>>(Tuple<String, String> parts) -> order.evaluate(parts.left(), parts.right(), leftRule, rightRule);
		};
	}

	private Result<Node, CompileError> evaluate(String left, String right) {
		return leftRule.lex(left).flatMap(leftNode -> rightRule.lex(right).mapValue(leftNode::merge));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return leftRule.generate(node)
									 .flatMap(left -> rightRule.generate(node).mapValue(right -> splitter.merge(left, right)));
	}
}
