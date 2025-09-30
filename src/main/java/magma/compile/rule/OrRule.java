package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record OrRule(List<Rule> rules) implements Rule {
	public static Rule Or(Rule... rules) {
		return new OrRule(Arrays.asList(rules));
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		final ArrayList<CompileError> errors = new ArrayList<>();
		for (Rule rule : rules)
			switch (rule.lex(content)) {
				case Ok<Node, CompileError> ok -> {
					return ok;
				}
				case Err<Node, CompileError>(CompileError error) -> errors.add(error);
			}
		return new Err<>(new CompileError("No alternative matched for input", new StringContext(content), errors));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		final ArrayList<CompileError> errors = new ArrayList<>();
		for (Rule rule : rules) {
			Result<String, CompileError> res = rule.generate(node);
			if (res instanceof Ok<String, CompileError> ok) return ok;
			else if (res instanceof Err<String, CompileError>(CompileError error)) errors.add(error);
		}
		return new Err<>(new CompileError("No generator matched for node", new NodeContext(node), errors));
	}
}
