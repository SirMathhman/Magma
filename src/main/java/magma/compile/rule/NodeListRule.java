package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.ArrayList;
import java.util.StringJoiner;

public record NodeListRule(String key, Rule rule, Divider divider) implements Rule {
	public static Rule Statements(String key, Rule rule) {
		return new NodeListRule(key, rule, new FoldingDivider(new EscapingFolder(new StatementFolder())));
	}

	public static Rule Delimited(String key, Rule rule, String delimiter) {
		return new NodeListRule(key, rule, new DelimitedRule(delimiter));
	}

	public static Rule Values(String key, Rule rule) {
		return new NodeListRule(key, rule, new FoldingDivider(new ValueFolder()));
	}

	@Override
	public Result<Node, CompileError> lex(String input) {
		final ArrayList<Node> children = new ArrayList<>(); for (String segment : divider.divide(input).toList()) {
			Result<Node, CompileError> res = rule().lex(segment);
			if (res instanceof Ok<Node, CompileError>(Node value)) children.add(value);
			else if (res instanceof Err<Node, CompileError>(CompileError error)) return new Err<>(error);
		}

		return new Ok<>(new Node().withNodeList(key, children));
	}

	@Override
	public Result<String, CompileError> generate(Node value) {
		Option<Result<String, CompileError>> resultOption = value.findNodeList(key).map(list -> {
			if (list.isEmpty()) return new Err<>(new CompileError("Node list for key '" + key +
																														"' is empty. If the intent is to have no items, then this property should be omitted entirely, as opposed to an empty list.",
																														new NodeContext(value)));

			final StringJoiner sb = new StringJoiner(divider.delimiter()); for (Node child : list)
				switch (this.rule.generate(child)) {
					case Ok<String, CompileError>(String value1) -> sb.add(value1);
					case Err<String, CompileError>(CompileError error) -> {
						return new Err<>(error);
					}
				}

			return new Ok<>(sb.toString());
		});

		return switch (resultOption) {
			case None<Result<String, CompileError>> _ ->
					new Err<>(new CompileError("Node list '" + key + "' not present", new NodeContext(value)));
			case Some<Result<String, CompileError>>(Result<String, CompileError> value2) -> value2;
		};
	}
}
