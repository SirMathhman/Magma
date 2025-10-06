package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.option.Option;
import magma.result.Err;
import magma.result.Result;

public class LazyRule implements Rule {
	private Option<Rule> maybeChild = new Option.None<>();

	public void set(Rule rule) {
		maybeChild = new Option.Some<>(rule);
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		return switch (maybeChild.map(child -> child.lex(content))) {
			case Option.None<Result<Node, CompileError>> _ ->
					new Err<>(new CompileError("Child not set", new StringContext(content)));
			case Option.Some<Result<Node, CompileError>> v -> v.value();
		};
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return switch (maybeChild.map(child -> child.generate(node))) {
			case Option.None<Result<String, CompileError>> _ -> new Err<>(new CompileError("Child not set", new NodeContext(node)));
			case Option.Some<Result<String, CompileError>> v -> v.value();
		};
	}
}
