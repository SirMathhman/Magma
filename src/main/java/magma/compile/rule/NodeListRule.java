package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.ArrayList;
import java.util.stream.Stream;

public record NodeListRule(String key, Rule rule) implements Rule {
	private static Stream<String> divide(String afterBraces) {
		final ArrayList<String> segments = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		int depth = 0;
		for (int i = 0; i < afterBraces.length(); i++) {
			final char c = afterBraces.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
			} else if (c == '}' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
				depth--;
			} else {
				if (c == '{') depth++;
				if (c == '}') depth--;
			}
		}

		segments.add(buffer.toString());
		return segments.stream();
	}

	public static Rule NodeList(String key, Rule rule) {
		return new NodeListRule(key, rule);
	}

	@Override
	public Result<Node, CompileError> lex(String input) {
		final ArrayList<Node> children = new ArrayList<>();
		final ArrayList<CompileError> errors = new ArrayList<>();
		divide(input).forEach(segment -> {
			Result<Node, CompileError> res = rule().lex(segment);
			if (res instanceof Ok<Node, CompileError>(Node value)) children.add(value);
			else if (res instanceof Err<Node, CompileError>(CompileError error)) errors.add(error);
		});
		if (!errors.isEmpty()) return new Err<>(
				new CompileError("Errors while lexing divided segments for '" + key + "'", new StringContext(input), errors));
		return new Ok<>(new Node().withNodeList(key, children));
	}

	@Override
	public Result<String, CompileError> generate(Node value) {
		return value.findNodeList(key()).<Result<String, CompileError>>map(list -> {
			final StringBuilder sb = new StringBuilder();
			for (Node child : list)
				switch (this.rule.generate(child)) {
					case Ok<String, CompileError>(String value1) -> sb.append(value1);
					case Err<String, CompileError>(CompileError error) -> {
						return new Err<>(error);
					}
				}

			return new Ok<>(sb.toString());
		}).orElseGet(() -> new Err<>(new CompileError("Node list '" + key + "' not present", new NodeContext(value))));
	}
}
