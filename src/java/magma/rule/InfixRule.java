package magma.rule;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.Optional;

public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {

	private Optional<Node> lex0(final String input) {
		final String infix1 = this.infix();
		final var index = input.indexOf(infix1);
		if (0 > index) return Optional.empty();
		final var left = input.substring(0, index).strip();
		final var right = input.substring(index + infix1.length());
		return this.leftRule.lex(left).findValue().flatMap(name -> {
			return this.rightRule.lex(right).findValue().map(name::merge);
		});
	}

	private Optional<String> generate0(final Node node) {
		return this.leftRule().generate(node).findValue().flatMap(leftResult -> {
			return this.rightRule().generate(node).findValue().map(rightResult -> leftResult + this.infix() + rightResult);
		});
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