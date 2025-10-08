package magma.compile.rule;

import magma.Tuple;
import magma.compile.Node;
import magma.compile.context.InputContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Some;
import magma.result.Err;
import magma.result.Result;

public record SplitRule(Rule leftRule, Rule rightRule, Splitter splitter, Order order) implements Rule {
	public sealed interface Order {
		Result<Node, CompileError> evaluate(Slice left, Slice right, Rule leftRule, Rule rightRule);
	}

	public static final class LeftFirst implements Order {
		@Override
		public Result<Node, CompileError> evaluate(Slice left, Slice right, Rule leftRule, Rule rightRule) {
			return leftRule.lex(left).flatMap(leftNode -> rightRule.lex(right).mapValue(leftNode::merge));
		}
	}

	public static final class RightFirst implements Order {
		@Override
		public Result<Node, CompileError> evaluate(Slice left, Slice right, Rule leftRule, Rule rightRule) {
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
	public Result<Node, CompileError> lex(Slice slice) {
		return switch (splitter.split(slice)) {
			case None<Tuple<Slice, Slice>> _ ->
					new Err<Node, CompileError>(new CompileError(splitter.createErrorMessage(), new InputContext(slice)));
			case Some<Tuple<Slice, Slice>>(Tuple<Slice, Slice> parts) ->
					order.evaluate(parts.left(), parts.right(), leftRule, rightRule);
		};
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return leftRule.generate(node)
									 .flatMap(left -> rightRule.generate(node).mapValue(right -> splitter.merge(left, right)));
	}
}
