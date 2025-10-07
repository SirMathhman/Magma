package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.InputContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Result;

public class FilterRule implements Rule {
	private final Filter filter;
	private final Rule rule;

	public FilterRule(Filter filter, Rule rule) {
		this.filter = filter; this.rule = rule;
	}

	public static Rule Filter(Filter filter, Rule rule) {
		return new FilterRule(filter, rule);
	}

	public static Rule Identifier(Rule rule) {
		return Filter(IdentifierFilter.Identifier, rule);
	}

	public static Rule Number(Rule rule) {
		return Filter(NumberFilter.Filter, rule);
	}

	@Override
	public Result<Node, CompileError> lex(TokenSequence content) {
		if (filter.test(content.value())) return rule.lex(content);
		return new Err<Node, CompileError>(new CompileError(filter.createErrorMessage(), new InputContext(content)));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return rule.generate(node);
	}
}
