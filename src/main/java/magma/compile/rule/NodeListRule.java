package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.InputContext;
import magma.compile.context.NodeContext;
import magma.compile.error.CompileError;
import magma.list.ArrayList;
import magma.list.List;
import magma.list.NonEmptyList;
import magma.option.None;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.StringJoiner;

/**
 * A rule that handles node lists.
 * Lists are only stored in Node.nodeLists when non-empty (enforced by
 * NonEmptyList type).
 * Generation fails when list is missing (allowing Or to try alternatives).
 * When list is present, iterates through each element, generates it, and joins
 * with the divider.
 *
 * @param key     the key for the node list
 * @param rule    the rule to apply to each element
 * @param divider the divider to use between elements
 */
public record NodeListRule(String key, Rule rule, Divider divider) implements Rule {
	public static Rule Statements(String key, Rule rule) {
		return new NodeListRule(key, rule, new FoldingDivider(new EscapingFolder(new StatementFolder())));
	}

	public static Rule Delimited(String key, Rule rule, String delimiter) {
		return new NodeListRule(key, rule, new DelimitedRule(delimiter));
	}

	public static Rule Expressions(String key, Rule rule) {
		return new NodeListRule(key, rule, new FoldingDivider(new EscapingFolder(new ValueFolder())));
	}

	/**
	 * Alias for creating a node list rule with an empty delimiter.
	 * Commonly used with Or to provide fallback when list is missing.
	 *
	 * @param key  the key for the node list
	 * @param rule the rule to apply to each element
	 * @return a node list rule with empty delimiter
	 */
	public static Rule NonEmptyList(String key, Rule rule) {
		return new NodeListRule(key, rule, new DelimitedRule(""));
	}

	@Override
	public Result<Node, CompileError> lex(Slice slice) {
		return divider.divide(slice)
									.reduce(new Ok<List<Node>, CompileError>(new ArrayList<Node>()), this::fold)
									.mapValue(list -> {
										// Only add to nodeLists if non-empty
										if (list.isEmpty()) return new Node();
										return NonEmptyList.fromList(list)
																			 .map(nonEmptyList -> new Node().withNodeList(key, nonEmptyList))
																			 .orElse(new Node()); // Should never happen since we checked isEmpty
									})
									.mapErr(err -> new CompileError("Failed to lex segments for key '" + key + "'",
																									new InputContext(slice),
																									List.of(err)));
	}

	private Result<List<Node>, CompileError> fold(Result<List<Node>, CompileError> current, Slice element) {
		return switch (current) {
			case Err<List<Node>, CompileError> v -> new Err<List<Node>, CompileError>(v.error());
			case Ok<List<Node>, CompileError>(List<Node> list) -> switch (rule.lex(element)) {
				case Err<Node, CompileError> v -> new Err<List<Node>, CompileError>(new CompileError("Failed to lex segment",
																																														 new InputContext(element),
																																														 List.of(v.error())));
				case Ok<Node, CompileError>(Node node) -> {
					list.addLast(node);
					yield new Ok<List<Node>, CompileError>(list);
				}
			};
		};
	}

	@Override
	public Result<String, CompileError> generate(Node value) {
		return switch (value.findNodeList(key)) {
			// List missing - fail to allow Or to try alternatives
			case None<?> _ -> new Err<String, CompileError>(new CompileError("Node list '" + key + "' not present",
																																			 new NodeContext(value)));
			// List present and non-empty - iterate and generate each element
			case Some<NonEmptyList<Node>>(NonEmptyList<Node> list) -> generateList(list);
		};
	}

	private Result<String, CompileError> generateList(NonEmptyList<Node> list) {
		final StringJoiner sb = new StringJoiner(divider.delimiter());
		int i = 0;
		while (i < list.size()) {
			switch (list.get(i)) {
				case Some<Node>(Node child) -> {
					switch (rule.generate(child)) {
						case Ok<String, CompileError>(String generated) -> sb.add(generated);
						case Err<String, CompileError>(CompileError error) -> {
							return new Err<String, CompileError>(error);
						}
					}
				}
				case None<?> _ -> {
					// Should never happen - NonEmptyList guarantees elements exist
					return new Err<String, CompileError>(new CompileError(
							"Unexpected missing element in NonEmptyList at index " + i, new NodeContext(list.first())));
				}
			}
			i++;
		}
		return new Ok<String, CompileError>(sb.toString());
	}

}
