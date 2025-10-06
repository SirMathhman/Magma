package magma.compile;

import magma.compile.rule.NodeRule;
import magma.compile.rule.Rule;

import static magma.compile.rule.EmptyRule.Empty;
import static magma.compile.rule.NodeListRule.Expressions;
import static magma.compile.rule.NodeListRule.Statements;
import static magma.compile.rule.NonEmptyListRule.NonEmptyList;
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
		final Rule body = Statements("body", Lang.CFunctionSegment());
		final Rule first = First(definition, "(", params);
		final Rule suffix = Suffix(first, ")");
		final Rule suffix1 = Suffix(body, System.lineSeparator() + "}");
		final Rule functionDecl = First(suffix, " {", suffix1);

		// Add template declaration only if type parameters exist (non-empty list)
		final Rule templateParams = Expressions("typeParameters", Prefix("typename ", CommonRules.Identifier()));
		final Rule templateDecl =
				NonEmptyList("typeParameters", Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())));
		final Rule maybeTemplate = Or(templateDecl, Empty);

		return Tag("function", First(maybeTemplate, "", functionDecl));
	}
}
