package magma.compile;

import magma.compile.rule.FilterRule;
import magma.compile.rule.LazyRule;
import magma.compile.rule.NodeListRule;
import magma.compile.rule.NodeRule;
import magma.compile.rule.Rule;
import magma.compile.rule.StringRule;
import magma.option.Option;

import java.util.ArrayList;
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
	sealed public interface JavaRootSegment permits Content, Import, JStructure, Package, Whitespace {}

	sealed public interface CRootSegment {
		Option<String> after();
	}

	public sealed interface JavaStructureSegment permits Content, JStructure, Method, Whitespace, Field {}

	sealed public interface JavaType {}

	sealed public interface CType {}

	sealed public interface JStructure extends JavaRootSegment, JavaStructureSegment permits Interface, JClass, Record {
		Option<String> modifiers();

		String name();

		List<JavaStructureSegment> children();
	}

	@Tag("statement")
	public record Field(JavaDefinition value) implements JavaStructureSegment {}

	@Tag("generic")
	public record Generic(String base, List<JavaType> arguments) implements JavaType {}

	@Tag("definition")
	public record JavaDefinition(String name, JavaType type) {}

	@Tag("method")
	public record Method(JavaDefinition definition, Option<List<JavaDefinition>> params, Option<String> body)
			implements JavaStructureSegment {}

	@Tag("content")
	public record Content(String value, Option<String> after)
			implements JavaRootSegment, JavaStructureSegment, CRootSegment, JavaType, CType {}

	@Tag("class")
	public record JClass(Option<String> modifiers, String name, List<JavaStructureSegment> children)
			implements JStructure {}

	@Tag("interface")
	public record Interface(Option<String> modifiers, String name, List<JavaStructureSegment> children)
			implements JStructure {}

	@Tag("record")
	public record Record(Option<String> modifiers, String name, List<JavaStructureSegment> children)
			implements JStructure {}

	@Tag("struct")
	public record Structure(String name, ArrayList<CDefinition> fields, Option<String> after) implements CRootSegment {}

	@Tag("whitespace")
	public record Whitespace() implements JavaRootSegment, JavaStructureSegment {}

	public record JavaRoot(List<JavaRootSegment> children) {}

	public record CRoot(List<CRootSegment> children) {}

	@Tag("import")
	public record Import(String value) implements JavaRootSegment {}

	@Tag("package")
	public record Package(String value) implements JavaRootSegment {}

	@Tag("definition")
	public record CDefinition(String name, CType type) {}

	@Tag("function")
	public record Function(CDefinition definition, List<CDefinition> params, String body, Option<String> after)
			implements CRootSegment {}

	@Tag("identifier")
	public record Identifier(String value) implements JavaType, CType {}

	public static Rule CRoot() {
		return Statements("children", Strip("", Or(CStructure(), Function(), Content()), "after"));
	}

	public static Rule Function() {
		return Tag("function",
							 First(Suffix(First(new NodeRule("definition", CDefinition()), "(", Values("params", CDefinition())),
														")"), " ", new StringRule("body")));
	}

	private static Rule CDefinition() {
		return Last(Node("type", CType()), " ", new StringRule("name"));
	}

	private static Rule CType() {
		return Or(Identifier(), Content());
	}

	private static Rule CStructure() {
		return Tag("struct", Prefix("struct ", Suffix(String("name"), "{};")));
	}

	public static Rule JavaRoot() {
		final Rule segment = Or(Namespace("package"), Namespace("import"), Structures(StructureMember()), Whitespace());
		return Statements("children", segment);
	}

	private static Rule Structures(Rule structureMember) {
		return Or(JStructure("class", structureMember),
							JStructure("interface", structureMember),
							JStructure("record", structureMember));
	}

	private static Rule Whitespace() {
		return Tag("whitespace", Strip(Empty));
	}

	private static Rule Namespace(String type) {
		return Tag(type, Strip(Prefix(type + " ", Suffix(Content(), ";"))));
	}

	private static Rule JStructure(String type, Rule rule) {
		final Rule modifiers = String("modifiers");
		final Rule name = Strip(String("name"));
		final Rule children = Statements("children", rule);

		final Rule aClass = First(First(Strip(Or(modifiers, Empty)), type + " ", name), "{", children);
		return Tag(type, Strip(Suffix(aClass, "}")));
	}

	private static Rule StructureMember() {
		final LazyRule structureMember = new LazyRule();
		structureMember.set(Or(Structures(structureMember), Statement(), Method(), Whitespace()));
		return structureMember;
	}

	private static Rule Statement() {
		return Tag("statement", Strip(Suffix(Node("value", JDefinition()), ";")));
	}

	private static Rule Method() {
		Rule params = Values("params", Or(JDefinition(), Whitespace()));
		final Rule header = Strip(Suffix(Last(Node("definition", JDefinition()), "(", params), ")"));
		final Rule withBody = Suffix(First(header, "{", String("body")), "}");
		return Tag("method", Strip(Or(Suffix(header, ";"), withBody)));
	}

	private static Rule JDefinition() {
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		final Rule type = Node("type", JavaType());
		final Rule last = Last(modifiers, " ", type);
		return Tag("definition", Last(Or(last, type), " ", String("name")));
	}

	private static Rule JavaType() {
		return Or(Generic(), Identifier(), Content());
	}

	private static Rule Identifier() {
		return Tag("identifier", Strip(FilterRule.Identifier(String("value"))));
	}

	private static Rule Generic() {
		return Tag("generic",
							 Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Values("arguments", Content())), ">")));
	}

	private static Rule Content() {
		return Tag("content", Placeholder(String("value")));
	}
}
