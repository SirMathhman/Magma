package magma.compile;

import magma.compile.rule.Rule;

import java.util.List;

import static magma.compile.rule.NodeListRule.NodeList;
import static magma.compile.rule.OrRule.Or;
import static magma.compile.rule.PlaceholderRule.Placeholder;
import static magma.compile.rule.StringRule.String;
import static magma.compile.rule.TagRule.Tag;

public class Lang {
	@Tag("content")
	public record Content(String value) {}

	public record JavaRoot(List<Content> children) {}

	public record CRoot(List<Content> children) {}

	public static Rule createCRootRule() {
		return NodeList("children", Content());
	}

	public static Rule createJavaRootRule() {
		return NodeList("children", Or(Content()));
	}

	private static Rule Content() {
		return Tag("content", Placeholder(String("value")));
	}
}
