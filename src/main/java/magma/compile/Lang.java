package magma.compile;

import magma.compile.rule.FilterRule;
import magma.compile.rule.FoldingDivider;
import magma.compile.rule.LazyRule;
import magma.compile.rule.NodeListRule;
import magma.compile.rule.NodeRule;
import magma.compile.rule.Rule;
import magma.compile.rule.SplitRule;
import magma.compile.rule.Splitter;
import magma.compile.rule.StringRule;
import magma.compile.rule.TypeFolder;
import magma.option.Option;

import java.util.List;

import static magma.compile.rule.DividingSplitter.KeepLast;
import static magma.compile.rule.EmptyRule.Empty;
import static magma.compile.rule.NodeListRule.*;
import static magma.compile.rule.NodeRule.Node;
import static magma.compile.rule.OrRule.Or;
import static magma.compile.rule.PlaceholderRule.Placeholder;
import static magma.compile.rule.PrefixRule.Prefix;
import static magma.compile.rule.SplitRule.First;
import static magma.compile.rule.SplitRule.Last;
import static magma.compile.rule.StringRule.String;
import static magma.compile.rule.StripRule.Strip;
import static magma.compile.rule.SuffixRule.Suffix;
import static magma.compile.rule.TagRule.Tag;

public class Lang {
	sealed public interface JavaRootSegment permits Invalid, Import, JStructure, Package, Whitespace {}

	sealed public interface CRootSegment permits Invalid, Structure, Function {
		Option<String> after();
	}

	public sealed interface JStructureSegment
			permits Invalid, JStructure, Method, Whitespace, Field, LineComment, BlockComment {}

	sealed public interface JavaType {}

	sealed public interface CType {}

	sealed public interface JStructure extends JavaRootSegment, JStructureSegment permits Interface, JClass, Record {
		Option<String> modifiers();

		String name();

		Option<List<Identifier>> typeParameters();

		List<JStructureSegment> children();
	}

	// Sealed interface for C parameter types
	public sealed interface CParameter permits CDefinition, CFunctionPointerDefinition {}

	@Tag("statement")
	public record Field(JavaDefinition value) implements JStructureSegment {}

	@Tag("generic")
	public record Generic(String base, List<JavaType> arguments) implements JavaType, CType {}

	@Tag("array")
	public record Array(JavaType child) implements JavaType {}

	@Tag("definition")
	public record JavaDefinition(String name, JavaType type, Option<List<Modifier>> modifiers,
															 Option<List<Identifier>> typeParameters) {}

	@Tag("modifier")
	public record Modifier(String value) {}

	@Tag("method")
	public record Method(JavaDefinition definition, Option<List<JavaDefinition>> params, Option<String> body,
											 Option<List<Identifier>> typeParameters) implements JStructureSegment {}

	@Tag("invalid")
	public record Invalid(String value, Option<String> after)
			implements JavaRootSegment, JStructureSegment, CRootSegment, JavaType, CType {}

	@Tag("class")
	public record JClass(Option<String> modifiers, String name, List<JStructureSegment> children,
											 Option<List<Identifier>> typeParameters, Option<JavaType> implementsClause)
			implements JStructure {}

	@Tag("interface")
	public record Interface(Option<String> modifiers, String name, List<JStructureSegment> children,
													Option<List<Identifier>> typeParameters, Option<JavaType> implementsClause)
			implements JStructure {}

	@Tag("record")
	public record Record(Option<String> modifiers, String name, List<JStructureSegment> children,
											 Option<List<Identifier>> typeParameters, Option<List<JavaDefinition>> params,
											 Option<JavaType> implementsClause) implements JStructure {}

	@Tag("struct")
	public record Structure(String name, List<CDefinition> fields, Option<String> after,
													Option<List<Identifier>> typeParameters) implements CRootSegment {}

	@Tag("whitespace")
	public record Whitespace() implements JavaRootSegment, JStructureSegment {}

	public record JavaRoot(List<JavaRootSegment> children) {}

	public record CRoot(List<CRootSegment> children) {}

	@Tag("import")
	public record Import(String value) implements JavaRootSegment {}

	@Tag("package")
	public record Package(String value) implements JavaRootSegment {}

	@Tag("definition")
	public record CDefinition(String name, CType type, Option<List<Identifier>> typeParameters) implements CParameter {}

	@Tag("functionPointerDefinition")
	public record CFunctionPointerDefinition(String name, CType returnType, List<CType> paramTypes)
			implements CParameter {}

	@Tag("function")
	public record Function(CDefinition definition, List<CParameter> params, String body, Option<String> after,
												 Option<List<Identifier>> typeParameters) implements CRootSegment {}

	@Tag("identifier")
	public record Identifier(String value) implements JavaType, CType {}

	@Tag("pointer")
	public record Pointer(CType child) implements CType {}

	@Tag("functionPointer")
	public record FunctionPointer(CType returnType, List<CType> paramTypes) implements CType {}

	@Tag("line-comment")
	public record LineComment(String value) implements JStructureSegment {}

	@Tag("block-comment")
	public record BlockComment(String value) implements JStructureSegment {}

	public static Rule CRoot() {
		return Statements("children", Strip("", Or(CStructure(), Function(), Invalid()), "after"));
	}

	public static Rule Function() {
		final NodeRule definition = new NodeRule("definition", CDefinition());
		final Rule params = Values("params", Or(CFunctionPointerDefinition(), CDefinition()));
		final Rule body = Placeholder(new StringRule("body"));
		final Rule functionDecl = First(Suffix(First(definition, "(", params), ")"), " {", Suffix(body, "}"));

		// Add template declaration if type parameters exist
		final Rule templateParams = Values("typeParameters", Prefix("typename ", Identifier()));
		final Rule templateDecl = Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator()));
		final Rule maybeTemplate = Or(templateDecl, new StringRule(""));

		return Tag("function", First(maybeTemplate, "", functionDecl));
	}

	private static Rule CFunctionPointerDefinition() {
		// Generates: returnType (*name)(paramTypes)
		return Tag("functionPointerDefinition",
							 Suffix(First(Suffix(First(Node("returnType", CType()), " (*", String("name")), ")("),
														"",
														Values("paramTypes", CType())), ")"));
	}

	private static Rule CDefinition() {
		return Last(Node("type", CType()), " ", new StringRule("name"));
	}

	private static Rule CType() {
		final LazyRule rule = new LazyRule();
		// Function pointer: returnType (*)(paramType1, paramType2, ...)
		final Rule funcPtr =
				Tag("functionPointer", Suffix(First(Node("returnType", rule), " (*)(", Values("paramTypes", rule)), ")"));
		rule.set(Or(funcPtr, Identifier(), Tag("pointer", Suffix(Node("child", rule), "*")), Generic(rule), Invalid()));
		return rule;
	}

	private static Rule CStructure() {
		// For template structs, use plain name without type parameters in the
		// declaration
		final Rule plainName = StrippedIdentifier("name");
		final Rule structPrefix = Prefix("struct ", plainName);
		final Rule fields = Values("fields", Suffix(CDefinition(), ";"));
		final Rule structWithFields = Suffix(First(structPrefix, "{", fields), "}");
		final Rule structComplete = Suffix(structWithFields, ";");

		// Add template declaration if type parameters exist
		final Rule templateParams = Values("typeParameters", Prefix("typename ", Identifier()));
		final Rule templateDecl = Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator()));
		final Rule maybeTemplate = Or(templateDecl, new StringRule(""));

		return Tag("struct", First(maybeTemplate, "", structComplete));
	}

	public static Rule JRoot() {
		final Rule segment =
				Or(Namespace("package"), Namespace("import"), Structures(StructureSegment()), BlockComment(), Whitespace());
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

		final Rule maybeWithParameters =
				Strip(Or(Suffix(First(maybeWithTypeArguments, "(", Parameters()), ")"), maybeWithTypeArguments));

		final Rule maybeWithParameters1 =
				Or(Last(maybeWithParameters, "extends", Node("extends", JType())), maybeWithParameters);

		final Rule beforeContent =
				Or(Last(maybeWithParameters1, "implements", Node("implementsClause", JType())), maybeWithParameters1);

		final Rule children = Statements("children", rule);

		final Rule beforeContent1 =
				Or(Last(beforeContent, "permits", Delimited("variants", StrippedIdentifier("variant"), ",")), beforeContent);

		final Rule aClass = First(First(Strip(Or(modifiers, Empty)), type + " ", beforeContent1), "{", children);
		return Tag(type, Strip(Suffix(aClass, "}")));
	}

	private static Rule NameWithTypeParameters() {
		final Rule name = StrippedIdentifier("name");
		final Rule withTypeParameters = Suffix(First(name, "<", Values("typeParameters", Identifier())), ">");
		return Strip(Or(withTypeParameters, name));
	}

	private static Rule StructureSegment() {
		final LazyRule structureMember = new LazyRule(); structureMember.set(Or(Structures(structureMember),
																																						Statement(),
																																						Method(),
																																						LineComment(),
																																						BlockComment(),
																																						Whitespace()));
		return structureMember;
	}

	private static Rule BlockComment() {
		return Tag("block-comment", Strip(Prefix("/*", Suffix(String("value"), "*/"))));
	}

	private static Rule LineComment() {
		return Tag("line-comment", Strip(Prefix("//", String("value"))));
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
		return Values("params", Or(ParameterDefinition(), Whitespace()));
	}

	private static Rule ParameterDefinition() {
		// Use TypeFolder to properly parse generic types like Function<T, R>
		// Parameters don't have modifiers, just type and name
		final FoldingDivider typeDivider = new FoldingDivider(new TypeFolder());
		final Splitter typeSplitter = KeepLast(typeDivider);

		return Tag("definition", new SplitRule(Node("type", JType()), String("name"), typeSplitter));
	}

	private static Rule JDefinition() {
		// Use TypeFolder to properly parse generic types like Function<T, R>
		// Split into modifiers+type and name using type-aware splitting
		final Rule typeAndName =
				new SplitRule(Node("type", JType()), String("name"), KeepLast(new FoldingDivider(new TypeFolder())));

		// Handle optional modifiers before type
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		final Rule withModifiers = Last(modifiers, " ", typeAndName);

		return Tag("definition", Or(withModifiers, typeAndName));
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
