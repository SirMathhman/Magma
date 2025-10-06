package magma.compile;

import magma.compile.rule.FoldingDivider;
import magma.compile.rule.LazyRule;
import magma.compile.rule.NodeListRule;
import magma.compile.rule.Rule;
import magma.compile.rule.TypeFolder;

import static magma.compile.rule.DividingSplitter.KeepLast;
import static magma.compile.rule.EmptyRule.Empty;
import static magma.compile.rule.NodeListRule.Delimited;
import static magma.compile.rule.NodeListRule.Expressions;
import static magma.compile.rule.NodeRule.Node;
import static magma.compile.rule.OrRule.Or;
import static magma.compile.rule.PrefixRule.Prefix;
import static magma.compile.rule.SplitRule.*;
import static magma.compile.rule.StringRule.String;
import static magma.compile.rule.StripRule.Strip;
import static magma.compile.rule.SuffixRule.Suffix;
import static magma.compile.rule.TagRule.Tag;

public class JRules {
	static Rule JDefinition() {
		// Use TypeFolder to properly parse generic types like Function<T, R>
		// Split into modifiers+type and name using type-aware splitting
		final Rule type = Node("type", JType());
		final Rule name = CommonRules.StrippedIdentifier("name");

		// Handle optional modifiers before type
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		final Rule withModifiers = Split(modifiers, KeepLast(new FoldingDivider(new TypeFolder())), type);

		Rule beforeName = Or(withModifiers, type);
		return Tag("definition", Strip(Last(beforeName, " ", name)));
	}

	static Rule JType() {
		final LazyRule type = new LazyRule();
		type.set(Or(JGeneric(type),
								JArray(type),
								CommonRules.Identifier(),
								JWildCard(),
								Tag("variadic", Strip(Suffix(Node("child", type), "..."))),
								QualifiedName()));
		return type;
	}

	private static Rule QualifiedName() {
		final Rule segment = Tag("segment", String("value"));
		return Tag("qualified", Strip(NodeListRule.Delimited("segments", segment, ".")));
	}

	private static Rule JWildCard() {
		return Tag("wildcard", Strip(Prefix("?", Empty)));
	}

	private static Rule JArray(Rule type) {
		return Tag("array", Strip(Suffix(Node("child", type), "[]")));
	}

	static Rule JGeneric(Rule type) {
		final Rule base = CommonRules.StrippedIdentifier("base");
		final Rule arguments = Or(Expressions("typeArguments", type), Strip(Empty));
		return Tag("generic", Strip(Suffix(First(base, "<", arguments), ">")));
	}
}
