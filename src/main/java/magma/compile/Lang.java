package magma.compile;

import magma.compile.rule.Rule;

import java.util.List;

import static magma.compile.rule.InfixRule.First;
import static magma.compile.rule.NodeListRule.NodeList;
import static magma.compile.rule.OrRule.Or;
import static magma.compile.rule.PlaceholderRule.Placeholder;
import static magma.compile.rule.PrefixRule.Prefix;
import static magma.compile.rule.StringRule.String;
import static magma.compile.rule.StripRule.Strip;
import static magma.compile.rule.SuffixRule.Suffix;
import static magma.compile.rule.TagRule.Tag;

public class Lang {
	sealed public interface JavaRootSegment {}

	sealed public interface CRootSegment {}

	@Tag("content")
	public record Content(String value) implements JavaRootSegment, CRootSegment {}

	@Tag("class")
	public record JClass(String name, List<Content> children) implements JavaRootSegment {}

	@Tag("struct")
	public record Structure(String name) implements CRootSegment {}

	public record JavaRoot(List<JavaRootSegment> children) {}

	public record CRoot(List<CRootSegment> children) {}

	public static Rule createCRootRule() {
		return NodeList("children", getOr());
	}

	private static Rule getOr() {
		return Or(Struct(), Content());
	}

	private static Rule Struct() {
		return Tag("struct", Prefix("struct ", Suffix(String("name"), "{};")));
	}

	public static Rule createJavaRootRule() {
		return NodeList("children", Or(Class(), Content()));
	}

	private static Rule Class() {
		final Rule modifiers = String("modifiers");
		final Rule name = String("name");
		final Rule children = NodeList("children", ClassMember());

		return Tag("class", Strip(Suffix(First(modifiers, "class ", First(name, "{", children)), "}")));
	}

	private static Rule ClassMember() {
		return Or(Tag("method", Strip(
				Suffix(First(Strip(Suffix(First(String("definition"), "(", String("params")), ")")), "{", String("body")),
							 "}"))), Content());
	}

	private static Rule Content() {
		return Tag("content", Placeholder(String("value")));
	}
}
