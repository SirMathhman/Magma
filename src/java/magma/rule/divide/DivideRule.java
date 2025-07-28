package magma.rule.divide;

import magma.error.CompileError;
import magma.node.MapNode;
import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import magma.rule.Rule;

import java.util.ArrayList;
import java.util.List;

public record DivideRule(String key, Rule rule) implements Rule {
	private static DivideState divide(final CharSequence input) {
		final var length = input.length();
		DivideState current = new MutableDivideState();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = DivideRule.fold(current, c);
		}

		return current.advance();
	}

	private static DivideState fold(final DivideState state, final char c) {
		final var appended = state.append(c);
		if (';' == c && appended.isLevel()) return appended.advance();
		if ('{' == c) return appended.enter();
		if ('}' == c) return appended.exit();
		return appended;
	}

	private Result<List<Node>, CompileError> foldAndAdd(final Result<List<Node>, CompileError> result,
																											final String segment) {
		return result.flatMapValue(list -> this.rule.lex(segment).mapValue(compiled -> {
			list.add(compiled);
			return list;
		}));
	}

	@Override
	public Result<Node, CompileError> lex(final String input) {
		return DivideRule.divide(input)
										 .stream()
										 .reduce(new Ok<>(new ArrayList<>()), this::foldAndAdd, (_, next) -> next)
										 .mapValue(children -> new MapNode().withNodeList(this.key, children));
	}

	@Override
	public Result<String, CompileError> generate(final Node node) {
		final var maybeNodeList = node.findNodeList(this.key);
		if (maybeNodeList.isEmpty()) return new Err<>(new CompileError());

		return maybeNodeList.get()
												.stream()
												.reduce(new Ok<>(new StringBuffer()), this::foldAndAppend, (_, next) -> next)
												.mapValue(StringBuffer::toString);
	}

	private Result<StringBuffer, CompileError> foldAndAppend(final Result<StringBuffer, CompileError> first,
																													 final Node second) {
		return first.and(() -> this.rule.generate(second)).mapValue(tuple -> tuple.left().append(tuple.right()));
	}
}