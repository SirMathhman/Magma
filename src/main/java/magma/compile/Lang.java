package magma.compile;

import magma.compile.rule.NodeListRule;
import magma.compile.rule.Rule;

import java.util.List;
import java.util.Optional;

import static magma.compile.rule.EmptyRule.Empty;
import static magma.compile.rule.InfixRule.First;
import static magma.compile.rule.InfixRule.Last;
import static magma.compile.rule.NodeListRule.*;
import static magma.compile.rule.NodeRule.Node;
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

	public sealed interface JavaClassMember {}

	@Tag("method")
	public record Method() implements JavaClassMember {}

	@Tag("content")
	public record Content(String value) implements JavaRootSegment, JavaClassMember, CRootSegment {}

	@Tag("class")
	public record JClass(Optional<String> modifiers, String name, List<JavaClassMember> children)
			implements JavaRootSegment {}

	@Tag("struct")
	public record Structure(String name) implements CRootSegment {}

	@Tag("whitespace")
	public record Whitespace() implements JavaRootSegment {}

	public record JavaRoot(List<JavaRootSegment> children) {}

	public record CRoot(List<CRootSegment> children) {}

	@Tag("import")
	public record Import(String value) implements JavaRootSegment {}

	@Tag("package")
	public record Package(String value) implements JavaRootSegment {}

	public static Rule createCRootRule() {
		return Statements("children", Or(Struct(), Content()));
	}

	private static Rule Struct() {
		return Tag("struct", Prefix("struct ", Suffix(String("name"), "{};")));
	}

	public static Rule createJavaRootRule() {
		return Statements("children",
											Or(Namespace("package"), Namespace("import"), Class(), Tag("whitespace", Strip(Empty))));
	}

	private static Rule Namespace(String type) {
		return Tag(type, Strip(Prefix(type + " ", Suffix(Content(), ";"))));
	}

	private static Rule Class() {
		final Rule modifiers = String("modifiers");
		final Rule name = String("name");
		final Rule children = Statements("children", ClassMember());

		final Rule aClass = First(First(Strip(Or(modifiers, Empty)), "class ", name), "{", children);
		return Tag("class", Strip(Suffix(aClass, "}")));
	}

	private static Rule ClassMember() {
		final Rule params = Or(Values("params", Definition()), Strip(Empty));
		return Or(Tag("method", Strip(
				Suffix(First(Strip(Suffix(Last(Node("definition", Definition()), "(", params), ")")), "{", String("body")),
							 "}"))), Content());
	}

	private static Rule Definition() {
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		return Last(Last(modifiers, " ", Node("type", Type())), " ", String("name"));
	}

	private static Rule Type() {
		return Or(Tag("generic",
									Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Values("arguments", Content())), ">"))),
							Content());
	}

	private static Rule Content() {
		return Tag("content", Placeholder(String("value")));
	}
}
