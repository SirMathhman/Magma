package magma.rule;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.Optional;

public record PlaceholderRule(Rule rule) implements Rule {
	public static String wrap(final String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}

	private Optional<Node> lex0(final String input) {
		return this.rule.lex(input).findValue();
	}

	private Optional<String> generate0(final Node node) {
		return this.rule().generate(node).findValue().map(PlaceholderRule::wrap);
	}

	@Override
	public Result<Node, CompileError> lex(final String input) {
		return this.lex0(input).<Result<Node, CompileError>>map(Ok::new).orElseGet(() -> new Err<>(new CompileError()));
	}

	@Override
	public Result<String, CompileError> generate(final Node node) {
		return this.generate0(node).<Result<String, CompileError>>map(Ok::new).orElseGet(() -> new Err<>(new CompileError()));
	}
}