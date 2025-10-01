package magma.compile;

import magma.compile.rule.NodeListRule;
import magma.compile.rule.NodeRule;
import magma.compile.rule.Rule;
import magma.compile.rule.StringRule;
import magma.option.Option;

import java.util.List;

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

	sealed public interface JavaType {}

	sealed public interface CType {}

	@Tag("generic")
	public record Generic(String base, List<JavaType> arguments) implements JavaType {}

	@Tag("definition")
	public record JavaDefinition(String name, JavaType type) {}

	@Tag("method")
	public record Method(JavaDefinition definition, Option<List<JavaDefinition>> params, Option<String> body)
			implements JavaClassMember {}

	@Tag("content")
	public record Content(String value) implements JavaRootSegment, JavaClassMember, CRootSegment, JavaType, CType {}

	@Tag("class")
	public record JClass(Option<String> modifiers, String name, List<JavaClassMember> children)
			implements JavaRootSegment {}

	@Tag("interface")
	public record Interface(Option<String> modifiers, String name, List<JavaClassMember> children)
			implements JavaRootSegment {}

	@Tag("record")
	public record Record(Option<String> modifiers, String name, List<JavaClassMember> children)
			implements JavaRootSegment {}

	@Tag("struct")
	public record Structure(String name) implements CRootSegment {}

	@Tag("whitespace")
	public record Whitespace() implements JavaRootSegment, JavaClassMember {}

	public record JavaRoot(List<JavaRootSegment> children) {}

	public record CRoot(List<CRootSegment> children) {}

	@Tag("import")
	public record Import(String value) implements JavaRootSegment {}

	@Tag("package")
	public record Package(String value) implements JavaRootSegment {}

	@Tag("definition")
	public record CDefinition(String name, CType type) {}

	@Tag("function")
	public record Function(CDefinition definition, List<CDefinition> params, String body) implements CRootSegment {}

	@Tag("identifier")
	public record Identifier(String value) implements JavaType, CType {}

	public static Rule createCRootRule() {
		return Statements("children", Or(Struct(), Function(), Content()));
	}

	public static Rule Function() {
		return Tag("function", new NodeRule("definition", Last(Node("type", CType()), " ", new StringRule("name"))));
	}

	private static Rule CType() {
		return Or(Identifier(), Content());
	}

	private static Rule Struct() {
		return Tag("struct", Prefix("struct ", Suffix(String("name"), "{};")));
	}

	public static Rule JavaRoot() {
		final Rule segment = Or(Namespace("package"), Namespace("import"), Structures(), Whitespace());

		return Statements("children", segment);
	}

	private static Rule Structures() {
		return Or(Structure("class"), Structure("interface"), Structure("record"));
	}

	private static Rule Whitespace() {
		return Tag("whitespace", Strip(Empty));
	}

	private static Rule Namespace(String type) {
		return Tag(type, Strip(Prefix(type + " ", Suffix(Content(), ";"))));
	}

	private static Rule Structure(String type) {
		final Rule modifiers = String("modifiers");
		final Rule name = String("name");
		final Rule children = Statements("children", ClassMember());

		final Rule aClass = First(First(Strip(Or(modifiers, Empty)), type + " ", name), "{", children);
		return Tag(type, Strip(Suffix(aClass, "}")));
	}

	private static Rule ClassMember() {
		return Or(Structures(), Method(), Whitespace());
	}

	private static Rule Method() {
		Rule params = Or(Values("params", Or(Definition(), Whitespace())), Strip(Empty));
		final Rule header = Strip(Suffix(Last(Node("definition", Definition()), "(", params), ")"));
		final Rule withBody = Suffix(First(header, "{", String("body")), "}");
		return Tag("method", Strip(Or(Suffix(header, ";"), withBody)));
	}

	private static Rule Definition() {
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		final Rule type = Node("type", JavaType());
		final Rule last = Last(modifiers, " ", type);
		return Tag("definition", Last(Or(last, type), " ", String("name")));
	}

	private static Rule JavaType() {
		return Or(Generic(), Identifier(), Content());
	}

	private static Rule Identifier() {
		return Tag("identifier", String("value"));
	}

	private static Rule Generic() {
		return Tag("generic",
							 Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Values("arguments", Content())), ">")));
	}

	private static Rule Content() {
		return Tag("content", Placeholder(String("value")));
	}
}
