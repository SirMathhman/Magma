package magma.compile;

import magma.compile.rule.LazyRule;
import magma.compile.rule.NodeRule;
import magma.compile.rule.Rule;

import static magma.compile.rule.EmptyRule.Empty;
import static magma.compile.rule.NodeListRule.*;
import static magma.compile.rule.OrRule.Or;
import static magma.compile.rule.PrefixRule.Prefix;
import static magma.compile.rule.SplitRule.First;
import static magma.compile.rule.StripRule.Strip;
import static magma.compile.rule.SuffixRule.Suffix;
import static magma.compile.rule.TagRule.Tag;

public class CRules {
	public static Rule CRoot() {
		return Statements("children", Strip("", Or(Lang.CStructure(), CFunction()), "after"));
	}

	public static Rule CFunction() {
		final NodeRule definition = new NodeRule("definition", Lang.CDefinition());
		final Rule params = Expressions("params", Or(Lang.CFunctionPointerDefinition(), Lang.CDefinition()));
		final Rule body = Statements("body", CFunctionSegment());
		final Rule first = First(definition, "(", Or(params, Empty));
		final Rule suffix = Suffix(first, ")");
		final Rule suffix1 = Suffix(body, System.lineSeparator() + "}");
		final Rule functionDecl = First(suffix, " {", suffix1);

		// Add template declaration only if type parameters exist (non-empty list)
		final Rule templateParams = Expressions("typeParameters", Prefix("typename ", CommonRules.Identifier()));
		final Rule templateDecl = NonEmptyList("typeParameters",
				Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())));
		final Rule maybeTemplate = Or(templateDecl, Empty);

		return Tag("function", First(maybeTemplate, "", functionDecl));
	}

	private static Rule CExpression() {
		LazyRule expression = new LazyRule();
		expression.set(Or(Lang.Invocation(expression),
				Lang.FieldAccess(expression),
				Lang.Operator("add", "+", expression),
				Lang.Operator("and", "&&", expression),
				Lang.Operator("equals", "==", expression),
				Lang.StringExpr(),
				CommonRules.Identifier(),
				Lang.Char(),
				Lang.Invalid()));
		return expression;
	}

	public static Rule CFunctionSegment() {
		final LazyRule rule = new LazyRule();
		rule.set(Or(Lang.Whitespace(), Prefix(System.lineSeparator() + "\t", CFunctionSegmentValue(rule)), Lang.Invalid()));
		return rule;
	}

	private static Rule CFunctionSegmentValue(LazyRule rule) {
		return Or(Lang.LineComment(),
				Lang.Conditional("if", CExpression(), rule),
				Lang.Conditional("while", CExpression(), rule),
				Lang.Break(),
				Lang.Else(rule),
				CFunctionStatement(),
				Lang.Block(rule));
	}

	private static Rule CFunctionStatement() {
		LazyRule functionStatement = new LazyRule();
		functionStatement.set(Or(Lang.Conditional("if", CExpression(), functionStatement),
				Suffix(CFunctionStatementValue(), ";")));
		return functionStatement;
	}

	private static Rule CFunctionStatementValue() {
		final Rule expression = CExpression();
		return Or(Lang.Return(expression),
				Lang.Invocation(expression),
				Lang.Initialization(Lang.CDefinition(), expression),
				Lang.CDefinition(),
				Lang.PostFix(expression));
	}
}
