package magma.compile.rule;

import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Result;

public record StripRule(String leftKey, Rule rule, String rightKey) implements Rule {
	public static Rule Strip(Rule rule) {
		return new StripRule("?", rule, "?");
	}

	public static Rule Strip(String left, Rule rule, String right) {
		return new StripRule(left, rule, right);
	}

	@Override
	public Result<Node, CompileError> lex(TokenSequence content) {
		return rule.lex(content.strip());
	}

	@Override
	public Result<TokenSequence, CompileError> generate(Node node) {
		return rule.generate(node).mapValue(generated -> {
			final String leftString = node.findSlice(leftKey).map(TokenSequence::value).orElse("");
			final String rightString = node.findSlice(rightKey).map(TokenSequence::value).orElse("");
			return leftString + generated + rightString;
		});
	}
}
