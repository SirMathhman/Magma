package magma.compile;

import magma.compile.rule.FilterRule;
import magma.compile.rule.Rule;

import static magma.compile.rule.StringRule.String;
import static magma.compile.rule.StripRule.Strip;
import static magma.compile.rule.TagRule.Tag;

public class CommonRules {
	public static Rule Identifier() {
		return Tag("identifier", Lang.StrippedIdentifier("value"));
	}

	static Rule StrippedIdentifier(String key) {
		return Strip(FilterRule.Identifier(String(key)));
	}
}
