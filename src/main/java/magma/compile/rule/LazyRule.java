package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.InputContext;
import magma.compile.context.NodeContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Result;

public class LazyRule implements Rule {
	private Option<Rule> maybeChild = new None<Rule>();

	public void set(Rule rule) {
		maybeChild = new Some<Rule>(rule);
	}

	@Override
	public Result<Node, CompileError> lex(Slice content) {
		return switch (maybeChild.map(child -> child.lex(content))) {
			case None<Result<Node, CompileError>> _ ->
					new Err<Node, CompileError>(new CompileError("Child not set", new InputContext(content)));
			case Some<Result<Node, CompileError>> v -> v.value();
		};
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return switch (maybeChild.map(child -> child.generate(node))) {
			case None<Result<String, CompileError>> _ -> new Err<String, CompileError>(new CompileError("Child not set", new NodeContext(node)));
			case Some<Result<String, CompileError>> v -> v.value();
		};
	}
}
