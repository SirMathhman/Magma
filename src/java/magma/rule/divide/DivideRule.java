package magma.rule.divide;

import magma.error.CompileError;
import magma.node.MapNode;
import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import magma.rule.Rule;

import java.util.Optional;

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

	private Optional<Node> lex0(final String input) {
		final var children = DivideRule.divide(input)
																	 .stream()
																	 .map(input1 -> this.rule.lex(input1).findValue())
																	 .flatMap(Optional::stream)
																	 .toList();

		return Optional.of(new MapNode().withNodeList(this.key, children));
	}

	@Override
	public Result<Node, CompileError> lex(final String input) {
		return this.lex0(input).<Result<Node, CompileError>>map(Ok::new).orElseGet(() -> new Err<>(new CompileError()));
	}

	@Override
	public Result<String, CompileError> generate(final Node node) {
		final var maybeNodeList = node.findNodeList(this.key);
		if (maybeNodeList.isEmpty()) return new Err<>(new CompileError());

		return maybeNodeList.get()
												.stream()
												.<Result<StringBuffer, CompileError>>reduce(new Ok<>(new StringBuffer()), this::fold,
																																		(_, next) -> next)
												.mapValue(StringBuffer::toString);
	}

	private Result<StringBuffer, CompileError> fold(final Result<StringBuffer, CompileError> first, final Node second) {
		return first.and(() -> this.rule.generate(second)).mapValue(tuple -> tuple.left().append(tuple.right()));
	}

}