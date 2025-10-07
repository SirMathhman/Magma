package magma.compile.rule;

import magma.compile.Node;
import magma.compile.collect.Accumulator;
import magma.compile.context.Context;
import magma.compile.context.NodeContext;
import magma.compile.context.TokenSequenceContext;
import magma.compile.error.CompileError;
import magma.list.List;
import magma.result.Result;

import java.util.function.Function;
import java.util.function.Supplier;

public record OrRule(List<Rule> rules) implements Rule {
	public static Rule Or(Rule... rules) {
		return new OrRule(List.of(rules));
	}

	@Override
	public Result<Node, CompileError> lex(TokenSequence content) {
		return foldAll(rule1 -> rule1.lex(content), () -> new TokenSequenceContext(content));
	}

	private <T> Result<T, CompileError> foldAll(Function<Rule, Result<T, CompileError>> mapper,
																							Supplier<Context> context) {
		return Accumulator.merge(rules, mapper)
											.mapErr(errors -> new CompileError("No alternative matched for input", context.get(), errors));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return foldAll(rule1 -> rule1.generate(node), () -> new NodeContext(node));
	}
}
