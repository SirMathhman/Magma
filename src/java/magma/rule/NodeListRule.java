package magma.rule;

import magma.divide.DivideState;
import magma.divide.MutableDivideState;
import magma.node.MapNode;
import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
		final var appended = state.append(c);
		if ('{' == c) return appended.enter();
		else if ('}' == c) return appended.exit();
		else if (';' == c && appended.isLevel())
			return appended.advance();
		return appended;
	}

	@Override
	public Result<String, String> generate(final Node node) {
		// Find the list of child nodes
		final Optional<List<Node>> maybeChildren = node.findNodeList(this.childrenKey); if (maybeChildren.isEmpty()) {
			return new Err<>("Node list not found for key: " + this.childrenKey);
		}

		// Generate text for each child node and combine the results
		final List<Node> children = maybeChildren.get(); final List<String> results = new ArrayList<>();

		for (Node child : children) {
			Result<String, String> childResult = this.childRule.generate(child); if (childResult.isOk()) {
				results.add(childResult.unwrap());
			}
		}

		if (results.isEmpty()) {
			return new Err<>("No child nodes could be generated");
		}

		return new Ok<>(String.join("", results));
	}

	@Override
	public Result<Node, String> lex(final String input) {
		// Divide the input into segments
		final Collection<String> segments = NodeListRule.divide(input);

		// Lex each segment and collect the results
		final List<Node> children = new ArrayList<>();

		for (String segment : segments) {
			Result<Node, String> childResult = this.childRule.lex(segment); if (childResult.isOk()) {
				children.add(childResult.unwrap());
			}
		}

		// If no segments were successfully lexed, return error
		if (children.isEmpty()) {
			return new Err<>("No segments could be lexed");
		}

		// Create a new node with the list of child nodes
		return new Ok<>(new MapNode().withNodeList(this.childrenKey, children));
	}
}