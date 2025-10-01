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
	sealed public interface JavaRootSegment permits Invalid, Import, JStructure, Package, Whitespace {
	}

	sealed public interface CRootSegment permits Invalid, Structure, Function {
		Option<String> after();
	}

	public sealed interface JavaStructureSegment permits Invalid, JStructure, Method, Whitespace, Field {
	}

	sealed public interface JavaType {
	}

	sealed public interface CType {
	}

	sealed public interface JStructure extends JavaRootSegment, JavaStructureSegment permits Interface, JClass, Record {
		Option<String> modifiers();

		String name();

		Option<List<String>> typeParameters();

		List<JavaStructureSegment> children();
	}

	@Tag("statement")
	public record Field(JavaDefinition value) implements JavaStructureSegment {
	}

	@Tag("generic")
	public record Generic(String base, List<JavaType> arguments) implements JavaType, CType {
	}

	@Tag("array")
	public record Array(JavaType child) implements JavaType {
	}

	@Tag("definition")
	public record JavaDefinition(String name, JavaType type, Option<List<Modifier>> modifiers) {
	}

	@Tag("modifier")
	public record Modifier(String value) {
	}

	@Tag("method")
	public record Method(JavaDefinition definition, Option<List<JavaDefinition>> params, Option<String> body)
			implements JavaStructureSegment {
	}

	@Tag("invalid")
	public record Invalid(String value, Option<String> after)
			implements JavaRootSegment, JavaStructureSegment, CRootSegment, JavaType, CType {
	}

	@Tag("class")
	public record JClass(Option<String> modifiers, String name, List<JavaStructureSegment> children,
			Option<List<String>> typeParameters, Option<JavaType> implementsClause)
			implements JStructure {
	}

	@Tag("interface")
	public record Interface(Option<String> modifiers, String name, List<JavaStructureSegment> children,
			Option<List<String>> typeParameters, Option<JavaType> implementsClause)
			implements JStructure {
	}

	@Tag("record")
	public record Record(Option<String> modifiers, String name, List<JavaStructureSegment> children,
			Option<List<String>> typeParameters, Option<List<JavaDefinition>> params, Option<JavaType> implementsClause)
			implements JStructure {
	}

	@Tag("struct")
	public record Structure(String name, ArrayList<CDefinition> fields, Option<String> after,
			Option<List<String>> typeParameters) implements CRootSegment {
	}

	@Tag("whitespace")
	public record Whitespace() implements JavaRootSegment, JavaStructureSegment {
	}

	public record JavaRoot(List<JavaRootSegment> children) {
	}

	public record CRoot(List<CRootSegment> children) {
	}

	@Tag("import")
	public record Import(String value) implements JavaRootSegment {
	}

	@Tag("package")
	public record Package(String value) implements JavaRootSegment {
	}

	@Tag("definition")
	public record CDefinition(String name, CType type) {
	}

	@Tag("function")
	public record Function(CDefinition definition, List<CDefinition> params, String body, Option<String> after)
			implements CRootSegment {
	}

	@Tag("identifier")
	public record Identifier(String value) implements JavaType, CType {
	}

	@Tag("pointer")
	public record Pointer(CType child) implements CType {
	}

	public static Rule CRoot() {
		return Statements("children", Strip("", Or(CStructure(), Function(), Invalid()), "after"));
	}

	public static Rule Function() {
		final NodeRule definition = new NodeRule("definition", CDefinition());
		final Rule params = Values("params", CDefinition());
		final Rule body = Placeholder(new StringRule("body"));
		return Tag("function", First(Suffix(First(definition, "(", params), ")"), " {", Suffix(body, "}")));
	}

	private static Rule CDefinition() {
		return Last(Node("type", CType()), " ", new StringRule("name"));
	}

	private static Rule CType() {
		final LazyRule rule = new LazyRule();
		rule.set(Or(Identifier(), Tag("pointer", Suffix(Node("child", rule), "*")), Generic(rule), Invalid()));
		return rule;
	}

	private static Rule CStructure() {
		return Tag("struct", Prefix("struct ", Suffix(NameWithTypeParameters(), "{};")));
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
		return Tag(type, Strip(Prefix(type + " ", Suffix(Invalid(), ";"))));
	}

	private static Rule JStructure(String type, Rule rule) {
		final Rule modifiers = String("modifiers");

		final Rule maybeWithTypeArguments = NameWithTypeParameters();

		final Rule maybeWithParameters = Strip(
				Or(Suffix(First(maybeWithTypeArguments, "(", Parameters()), ")"), maybeWithTypeArguments));

		final Rule maybeWithParameters1 = Or(Last(maybeWithParameters, "extends", Node("extends", JType())),
				maybeWithParameters);

		final Rule beforeContent = Or(Last(maybeWithParameters1, "implements", Node("implementsClause", JType())),
				maybeWithParameters1);

		final Rule children = Statements("children", rule);

		final Rule beforeContent1 = Or(
				Last(beforeContent, " permits ", Delimited("variants", StrippedIdentifier("variant"), ",")), beforeContent);

		final Rule aClass = First(First(Strip(Or(modifiers, Empty)), type + " ", beforeContent1), "{", children);
		return Tag(type, Strip(Suffix(aClass, "}")));
	}

	private static Rule NameWithTypeParameters() {
		final Rule name = StrippedIdentifier("name");
		final Rule withTypeParameters = Suffix(First(name, "<", Values("typeParameters", Identifier())), ">");
		return Strip(Or(withTypeParameters, name));
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
		Rule params = Parameters();
		final Rule header = Strip(Suffix(Last(Node("definition", JDefinition()), "(", params), ")"));
		final Rule withBody = Suffix(First(header, "{", String("body")), "}");
		return Tag("method", Strip(Or(Suffix(header, ";"), withBody)));
	}

	private static Rule Parameters() {
		return Values("params", Or(JDefinition(), Whitespace()));
	}

	private static Rule JDefinition() {
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		final Rule type = Node("type", JType());
		final Rule last = Last(modifiers, " ", type);
		return Tag("definition", Last(Or(last, type), " ", String("name")));
	}

	private static Rule JType() {
		final LazyRule type = new LazyRule();
		type.set(Or(Generic(type), Array(type), Identifier(), Invalid()));
		return type;
	}

	private static Rule Array(Rule type) {
		return Tag("array", Strip(Suffix(Node("child", type), "[]")));
	}

	private static Rule Identifier() {
		return Tag("identifier", StrippedIdentifier("value"));
	}

	private static Rule StrippedIdentifier(String key) {
		return Strip(FilterRule.Identifier(String(key)));
	}

	private static Rule Generic(Rule type) {
		return Tag("generic",
				Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Values("arguments", type)), ">")));
	}

	private static Rule Invalid() {
		return Tag("invalid", Placeholder(String("value")));
	}
}
