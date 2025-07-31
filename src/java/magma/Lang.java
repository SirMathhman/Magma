package magma;

import magma.rule.InfixRule;
import magma.rule.NodeListRule;
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
	static NodeListRule createJavaRootRule() {
		return new NodeListRule(Lang.createJavaRootSegmentRule(), "children");
	}

	private static OrRule createTSRootSegmentRule() {
		return new OrRule(List.of(Lang.createTSClassRule(), Lang.createTypedPlaceholderRule()));
	}

	private static OrRule createJavaRootSegmentRule() {
		return new OrRule(
				List.of(Lang.createNamespacedRule("package"), Lang.createNamespacedRule("import"), Lang.createJavaClassRule(),
								Lang.createTypedPlaceholderRule()));
	}

	private static TypeRule createTypedPlaceholderRule() {
		return new TypeRule("placeholder", new PlaceholderRule(new StringRule("content")));
	}

	private static Rule createNamespacedRule(final String type) {
		final var content = new PrefixRule(type + " ", new SuffixRule(new StringRule("content"), ";"));
		return new TypeRule(type, new StripRule(content));
	}

	private static InfixRule createTSClassRule() {
		final Rule name = new StringRule("name");
		final Rule body = new NodeListRule(Lang.createTSClassMemberRule(), "children");

		return new InfixRule(new SuffixRule(new PrefixRule("export class ", name), " {"), "",
												 new SuffixRule(new PlaceholderRule(body), "}"));
	}

	private static TypeRule createTSClassMemberRule() {
		return Lang.createTypedPlaceholderRule();
	}

	private static InfixRule createJavaClassRule() {
		final Rule name = new StringRule("name");
		final Rule body = new NodeListRule(Lang.createJavaClassMemberRule(), "children");

		// More flexible rule that can handle modifiers before "class"
		// and additional content between class name and opening brace
		return new InfixRule(new StringRule("modifiers"), "class ",
												 new InfixRule(name, "{", new StripRule(new SuffixRule(body, "}"))));
	}

	private static TypeRule createJavaClassMemberRule() {
		return Lang.createTypedPlaceholderRule();
	}

	static NodeListRule createTSRootRule() {
		return new NodeListRule(Lang.createTSRootSegmentRule(), "children");
	}
}
