package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.ArrayList;
import java.util.List;
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
									.reduce(new Ok<>(new ArrayList<>()), this::fold, (_, next) -> next)
									.mapValue(list -> new Node().withNodeList(key, list))
									.mapErr(err -> new CompileError("Failed to lex segments for key '" + key + "'",
																									new StringContext(input),
																									List.of(err)));
	}

	private Result<List<Node>, CompileError> fold(Result<List<Node>, CompileError> current, String element) {
		return switch (current) {
			case Err<List<Node>, CompileError> v -> new Err<>(v.error());
			case Ok<List<Node>, CompileError>(List<Node> list) -> switch (rule.lex(element)) {
				case Err<Node, CompileError> v ->
						new Err<>(new CompileError("Failed to lex segment", new StringContext(element), List.of(v.error())));
				case Ok<Node, CompileError>(Node node) -> {
					list.add(node);
					yield new Ok<>(list);
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
			case None<Result<String, CompileError>> _ -> new Ok<>("");
			case Some<Result<String, CompileError>>(Result<String, CompileError> value2) -> value2;
		};
	}

	private Result<String, CompileError> generateList(List<Node> list) {
		// Treat missing or empty lists as empty content when generating.
		if (list.isEmpty()) return new Ok<>("");

		final StringJoiner sb = new StringJoiner(divider.delimiter());
		int i = 0;
		while (i < list.size()) {
			Node child = list.get(i);
			switch (this.rule.generate(child)) {
				case Ok<String, CompileError>(String value1) -> sb.add(value1);
				case Err<String, CompileError>(CompileError error) -> {
					return new Err<>(error);
				}
			}
			i++;
		}

		return new Ok<>(sb.toString());
	}
}
