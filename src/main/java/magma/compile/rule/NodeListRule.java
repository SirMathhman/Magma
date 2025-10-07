package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.list.ArrayList;
import magma.list.List;
import magma.list.NonEmptyList;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.StringJoiner;

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

	@Override
	public Result<Node, CompileError> lex(String input) {
		return divider.divide(input)
				.reduce(new Ok<List<Node>, CompileError>(new ArrayList<Node>()), this::fold)
				.mapValue(list -> {
					// Only add to nodeLists if non-empty
					if (list.isEmpty())
						return new Node();
					return NonEmptyList.fromList(list)
							.map(nonEmptyList -> new Node().withNodeList(key, nonEmptyList))
							.orElse(new Node()); // Should never happen since we checked isEmpty
				})
				.mapErr(err -> new CompileError("Failed to lex segments for key '" + key + "'",
						new StringContext(input),
						List.of(err)));
	}

	private Result<List<Node>, CompileError> fold(Result<List<Node>, CompileError> current, String element) {
		return switch (current) {
			case Err<List<Node>, CompileError> v -> new Err<List<Node>, CompileError>(v.error());
			case Ok<List<Node>, CompileError>(List<Node> list) -> switch (rule.lex(element)) {
				case Err<Node, CompileError> v -> new Err<List<Node>, CompileError>(new CompileError("Failed to lex segment",
						new StringContext(element),
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
		Option<Result<String, CompileError>> resultOption = value.findNodeList(key).map(this::generateList);

		return switch (resultOption) {
			// If the node-list isn't present at all, treat it as empty rather than an
			// error.
			case None<Result<String, CompileError>> _ -> new Ok<String, CompileError>("");
			case Some<Result<String, CompileError>>(Result<String, CompileError> value2) -> value2;
		};
	}

	private Result<String, CompileError> generateList(NonEmptyList<Node> list) {
		// NonEmptyList is never empty, so no isEmpty check needed
		final StringJoiner sb = new StringJoiner(divider.delimiter());
		int i = 0;
		while (i < list.size()) {
			Node child = list.get(i).orElse(null);
			switch (this.rule.generate(child)) {
				case Ok<String, CompileError>(String value1) -> sb.add(value1);
				case Err<String, CompileError>(CompileError error) -> {
					return new Err<String, CompileError>(error);
				}
			}
			i++;
		}

		return new Ok<String, CompileError>(sb.toString());
	}
}
