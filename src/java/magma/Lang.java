package magma;

import magma.rule.DivideRule;
import magma.rule.InfixRule;
import magma.rule.OrRule;
import magma.rule.PlaceholderRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;

import java.util.List;

class Lang {
	static DivideRule createJavaRootRule() {
		return new DivideRule(Lang.createJavaRootSegmentRule());
	}

	private static OrRule createTSRootSegmentRule() {
		return new OrRule(List.of(Lang.createTSClassRule(), Lang.createTypePlaceholderRule()));
	}

	private static OrRule createJavaRootSegmentRule() {
		return new OrRule(
				List.of(Lang.createNamespacedRule("package"), Lang.createNamespacedRule("import"), Lang.createJavaClassRule(),
								Lang.createTypePlaceholderRule()));
	}

	private static TypeRule createTypePlaceholderRule() {
		return new TypeRule("placeholder", new PlaceholderRule(new StringRule("content")));
	}

	private static Rule createNamespacedRule(final String type) {
		final var content = new PrefixRule(type + " ", new SuffixRule(new StringRule("content"), ";"));
		return new TypeRule(type, new StripRule(content));
	}

	private static InfixRule createTSClassRule() {
		final Rule name = new StringRule("name");
		final Rule body = new StringRule("body");

		return new InfixRule(new SuffixRule(new PrefixRule("export class ", name), " {"), "",
												 new SuffixRule(new PlaceholderRule(body), "}"));
	}

	private static InfixRule createJavaClassRule() {
		final Rule name = new StringRule("name");
		final Rule body = new StringRule("body");

		// More flexible rule that can handle modifiers before "class"
		// and additional content between class name and opening brace
		return new InfixRule(new StringRule("modifiers"), "class ",
												 new InfixRule(name, "{", new StripRule(new SuffixRule(body, "}"))));
	}

	static DivideRule createTSRootRule() {
		return new DivideRule(Lang.createTSRootSegmentRule());
	}
}
