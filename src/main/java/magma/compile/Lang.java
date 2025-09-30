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
	public record JClass(String name) implements JavaRootSegment {}

	@Tag("struct")
	public record Structure(String name) implements CRootSegment {}

	public record JavaRoot(List<JavaRootSegment> children) {}

	public record CRoot(List<CRootSegment> children) {}

	public static Rule createCRootRule() {
		return NodeList("children", Or(Tag("struct", Prefix("struct ", Suffix(String("name"), "{};"))), Content()));
	}

	public static Rule createJavaRootRule() {
		return NodeList("children", Or(Class(), Content()));
	}

	private static Rule Class() {
		return Tag("class",
							 Strip(Suffix(First(String("modifiers"), "class ", First(String("name"), "{", Content())), "}")));
	}

	private static Rule Content() {
		return Tag("content", Placeholder(String("value")));
	}
}
