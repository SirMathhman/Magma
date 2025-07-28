/*import magma.rule.divide.divide.DivideRule;*/
/*import magma.rule.InfixRule;*/
/*import magma.rule.OrRule;*/
/*import magma.rule.PlaceholderRule;*/
/*import magma.rule.PrefixRule;*/
/*import magma.rule.Rule;*/
/*import magma.rule.StringRule;*/
/*import magma.rule.StripRule;*/
/*import magma.rule.SuffixRule;*/
/*import magma.rule.TypeRule;*/
/*import java.util.List;*/
/*public*/class Lang {/*static DivideRule createTSRootRule() {
		return new DivideRule("children", new SuffixRule(Lang.createTSRootSegmentValueRule(), System.lineSeparator()));
	}

	static DivideRule createJavaRootRule() {
		return new DivideRule("children", Lang.createJavaRootSegmentValueRule());
	}

	private static Rule createJavaRootSegmentValueRule() {
		return new OrRule(
				List.of(Lang.createJavaClassRule(), Lang.createPackageRule(), new StripRule(Lang.createPlaceholderRule())));
	}

	private static TypeRule createPackageRule() {
		return new TypeRule("package", new StripRule(new PrefixRule("package ", new StringRule("value"))));
	}

	private static Rule createTSRootSegmentValueRule() {
		return new OrRule(List.of(Lang.createTSClassRule(), Lang.createPlaceholderRule()));
	}

	private static Rule createPlaceholderRule() {
		return new TypeRule("placeholder", new PlaceholderRule(new StringRule("value")));
	}

	private static Rule createJavaClassRule() {
		final var modifiers1 = new StringRule("modifiers");
		final var name =
				new InfixRule(new StringRule("name"), "{", new StripRule(new SuffixRule(new StringRule("with-end"), "}")));
		return new TypeRule("class", new InfixRule(modifiers1, "class ", name));
	}

	private static Rule createTSClassRule() {
		final var content = new PlaceholderRule(new StringRule("with-end"));
		final var name = new InfixRule(new StringRule("name"), " {", new SuffixRule(content, "}"));
		final var modifiers = new PlaceholderRule(new StringRule("modifiers"));
		return new TypeRule("class", new InfixRule(modifiers, "class ", name));
	}
*/}
