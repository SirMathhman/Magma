package magma.rule.divide;

import magma.error.CompileError;
import magma.input.Input;
import magma.input.StringInput;
import magma.node.MapNode;
import magma.node.Node;
import magma.result.Ok;
import magma.result.Result;
import magma.rule.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Rule that divides input text into segments, lexes each segment, and combines the results into a node with a list of child nodes.
 * When generating, it generates text for each child node and combines the results.
 */
public final class NodeListRule implements Rule {
	private final Rule childRule;
	private final String childrenKey;

	/**
	 * Creates a new DivideRule with the specified child rule and children key.
	 *
	 * @param childRule   the rule to apply to each segment
	 * @param childrenKey the key to use for the list of child nodes
	 */
	public NodeListRule(final Rule childRule, final String childrenKey) {
		this.childRule = childRule;
		this.childrenKey = childrenKey;
	}

	/**
	 * Divides the input into segments based on braces and semicolons.
	 * This is similar to the divide method in Main.java.
	 *
	 * @param input the input text
	 * @return a collection of segments
	 */
	private static Collection<String> divide(final String input) {
		DivideState current = new MutableDivideState(input);
		while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) break;
			final var next = maybeNext.get();
			current = NodeListRule.fold(next.left(), next.right());
		}

		return current.advance().stream().toList();
	}

	/**
	 * Helper method for divide that handles state transitions.
	 * This is similar to the fold method in Main.java.
	 *
	 * @param state the current state
	 * @param c     the current character
	 * @return the updated state
	 */
	private static DivideState fold(final DivideState state, final char c) {
		final var appended = state.append(c); if ('{' == c) return appended.enter();
		if ('}' == c && appended.isShallow()) return appended.advance().exit(); if ('}' == c) return appended.exit();
		if (';' == c && appended.isLevel()) return appended.advance();
		return appended;
	}

	@Override
	public Result<String, CompileError> generate(final Node node) {
		return Result.fromOptional(node.findNodeList(this.childrenKey),
															 () -> CompileError.forGeneration("Node list not found for key: " + this.childrenKey,
																																node))
								 .flatMap(this::combineChildrenResults)
								 .map(StringBuilder::toString);
	}

	private Result<StringBuilder, CompileError> combineChildrenResults(final Collection<Node> children) {
		return children.stream().reduce(new Ok<>(new StringBuilder()), this::appendChildResult, (_, result) -> result);
	}

	private Result<StringBuilder, CompileError> appendChildResult(final Result<StringBuilder, CompileError> result,
																																final Node child) {
		return result.and(() -> this.childRule.generate(child)).mapValue(tuple -> tuple.left().append(tuple.right()));
	}

	@Override
	public Result<Node, CompileError> lex(final Input input) {
		// Divide the input into segments
		final Collection<String> segments = NodeListRule.divide(input.getContent());

		// Lex each segment and collect the results
		final Result<List<Node>, CompileError> nodesResult = new Ok<>(new ArrayList<>());
		Result<List<Node>, CompileError> result = nodesResult;

		// Track position in the original input
		int currentPosition = input.getStartIndex();
		
		for (String segment : segments) {
			// Calculate segment positions
			int segmentStart = currentPosition; int segmentEnd = segmentStart + segment.length();
			currentPosition = segmentEnd;

			final StringInput segmentInput =
					new StringInput(segment, input.getSource() + " (segment)", segmentStart, segmentEnd);
			result = result.and(() -> this.childRule.lex(segmentInput)).mapValue(tuple -> {
				tuple.left().add(tuple.right()); return tuple.left();
			});

			if (result.isErr()) {
				return result.mapValue(nodes -> null); // This will never be called due to isErr() check
			}
		}

		return result.mapValue(list -> new MapNode().withNodeList(this.childrenKey, list));
	}
}