package magma.compile.rule;

import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.list.List;
import magma.result.Result;

public final class OptionalNodeListRule implements Rule {
	private final String key;
	private final Rule ifPresent;
	private final Rule ifEmpty;
	private OrRule lexRule;

	public OptionalNodeListRule(String key, Rule ifPresent, Rule ifEmpty) {
		this.key = key;
		this.ifPresent = ifPresent;
		this.ifEmpty = ifEmpty;
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		lexRule = new OrRule(List.of(ifPresent, ifEmpty));
		return lexRule.lex(content);
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		if (node.hasNodeList(key)) return ifPresent.generate(node);
		else return ifEmpty.generate(node);
	}
}
