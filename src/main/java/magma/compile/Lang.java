package magma.compile;

import magma.compile.rule.BraceStartFolder;
import magma.compile.rule.ClosingParenthesesFolder;
import magma.compile.rule.DividingSplitter;
import magma.compile.rule.EscapingFolder;
import magma.compile.rule.FilterRule;
import magma.compile.rule.FoldingDivider;
import magma.compile.rule.LazyRule;
import magma.compile.rule.NodeRule;
import magma.compile.rule.Rule;
import magma.compile.rule.SplitRule;
import magma.compile.rule.Splitter;
import magma.compile.rule.StringRule;
import magma.compile.rule.TypeFolder;
import magma.option.None;
import magma.option.Option;

import java.util.List;

import static magma.compile.rule.DividingSplitter.KeepFirst;
import static magma.compile.rule.DividingSplitter.KeepLast;
import static magma.compile.rule.EmptyRule.Empty;
import static magma.compile.rule.NodeListRule.*;
import static magma.compile.rule.NodeRule.Node;
import static magma.compile.rule.NonEmptyListRule.NonEmptyList;
import static magma.compile.rule.OrRule.Or;
import static magma.compile.rule.PlaceholderRule.Placeholder;
import static magma.compile.rule.PrefixRule.Prefix;
import static magma.compile.rule.SplitRule.*;
import static magma.compile.rule.StringRule.String;
import static magma.compile.rule.StripRule.Strip;
import static magma.compile.rule.SuffixRule.Suffix;
import static magma.compile.rule.TagRule.Tag;

public class Lang {
	sealed public interface JavaRootSegment permits Invalid, Import, JStructure, Package, Whitespace, BlockComment {
	}

	sealed public interface CRootSegment permits Invalid, Structure, Function {
		Option<String> after();
	}

	public sealed interface JStructureSegment
			permits Invalid, JStructure, Method, Whitespace, Field, LineComment, BlockComment {
	}

	sealed public interface JExpression permits Identifier, Invalid, JAdd, JConstruction, JEquals, JFieldAccess, JInvocation, JString, Switch {
	}

	@Tag("add")
	public record JAdd(JExpression left, JExpression right) implements JExpression {
	}

	@Tag("equals")
	public record JEquals(JExpression left, JExpression right) implements JExpression {
	}

	@Tag("equals")
	public record CEquals(CExpression left, CExpression right) implements CExpression {
	}

	@Tag("string")
	public record JString(Option<String> content) implements JExpression {
	}

	@Tag("add")
	public record CAdd(CExpression left, CExpression right) implements CExpression {
	}

	@Tag("string")
	public record CString(String content) implements CExpression {
	}

	sealed public interface JMethodSegment
			permits Break, Invalid, JAssignment, JBlock, JConstruction, JElse, JIf, JInitialization, JInvocation, JInvokable,
			JPostFix, JReturn, JWhile, LineComment, Placeholder, Whitespace, JDefinition {
	}

	sealed public interface CFunctionSegment
			permits Break, CAssignment, CBlock, CDefinition, CElse, CIf, CInitialization, CInvokable, CPostFix, CReturn, CWhile, Invalid, LineComment, Placeholder, Whitespace {
	}

	sealed public interface JType {
	}

	sealed public interface CType {
	}

	sealed public interface JStructure extends JavaRootSegment, JStructureSegment permits Interface, JClass, Record {
		String name();

		Option<List<Identifier>> typeParameters();

		List<JStructureSegment> children();
	}

	// Sealed interface for C parameter types
	public sealed interface CParameter permits CDefinition, CFunctionPointerDefinition {
	}

	public sealed interface CExpression permits CAdd, CEquals, CFieldAccess, CString, Identifier, Invalid {
	}

	@Tag("field-access")
	public record JFieldAccess(JExpression child, String name) implements JExpression {
	}

	@Tag("field-access")
	public record CFieldAccess(CExpression child, String name) implements CExpression {
	}

	@Tag("construction")
	public record JConstruction(JType type, Option<List<JExpression>> arguments) implements JExpression, JMethodSegment {
	}

	@Tag("invocation")
	public record JInvocation(JExpression caller, Option<List<JExpression>> arguments)
			implements JExpression, JMethodSegment {
	}

	@Tag("case")
	public record Case(JDefinition definition, JExpression value) {
	}

	@Tag("switch")
	public record Switch(JExpression value, List<Case> cases) implements JExpression {
	}

	@Tag("assignment")
	public record CAssignment(CExpression location, CExpression value) implements CFunctionSegment {
	}

	@Tag("postFix")
	public record CPostFix(CExpression value) implements CFunctionSegment {
	}

	@Tag("assignment")
	public record JAssignment(JExpression location, JExpression value) implements JMethodSegment {
	}

	@Tag("postFix")
	public record JPostFix(JExpression value) implements JMethodSegment {
	}

	@Tag("initialization")
	public record JInitialization(JDefinition definition, JExpression value) implements JMethodSegment {
	}

	@Tag("initialization")
	public record CInitialization(CDefinition definition, CExpression value) implements CFunctionSegment {
	}

	@Tag("block")
	public record CBlock(List<CFunctionSegment> children) implements CFunctionSegment {
	}

	@Tag("block")
	public record JBlock(List<JMethodSegment> children) implements JMethodSegment {
	}

	@Tag("if")
	public record JIf(JExpression condition, JMethodSegment body) implements JMethodSegment {
	}

	@Tag("if")
	public record CIf(CExpression condition, CFunctionSegment body) implements CFunctionSegment {
	}

	@Tag("while")
	public record JWhile(JExpression condition, JMethodSegment body) implements JMethodSegment {
	}

	@Tag("while")
	public record CWhile(CExpression condition, CFunctionSegment body) implements CFunctionSegment {
	}

	@Tag("statement")
	public record Field(JDefinition value) implements JStructureSegment {
	}

	@Tag("generic")
	public record JGeneric(String base, Option<List<JType>> typeArguments) implements JType {
	}

	@Tag("generic")
	public record CGeneric(String base, List<CType> typeArguments) implements CType {
	}

	@Tag("array")
	public record Array(JType child) implements JType {
	}

	@Tag("definition")
	public record JDefinition(String name, JType type, Option<List<Modifier>> modifiers,
														Option<List<Identifier>> typeParameters) implements JMethodSegment {
	}

	@Tag("modifier")
	public record Modifier(String value) {
	}

	@Tag("method")
	public record Method(JDefinition definition, Option<List<JDefinition>> params, Option<List<JMethodSegment>> body,
											 Option<List<Identifier>> typeParameters) implements JStructureSegment {
	}

	@Tag("invalid")
	public record Invalid(String value, Option<String> after)
			implements JavaRootSegment, JStructureSegment, CRootSegment, JType, CType, JMethodSegment, CFunctionSegment,
			JExpression, CExpression {
		public Invalid(String value) {
			this(value, new None<>());
		}
	}

	@Tag("class")
	public record JClass(Option<String> modifiers, String name, List<JStructureSegment> children,
											 Option<List<Identifier>> typeParameters, Option<JType> implementsClause) implements JStructure {
	}

	@Tag("interface")
	public record Interface(Option<String> modifiers, String name, List<JStructureSegment> children,
													Option<List<Identifier>> typeParameters, Option<JType> implementsClause,
													Option<JType> extendsClause, Option<List<JType>> variants) implements JStructure {
	}

	@Tag("record")
	public record Record(Option<String> modifiers, String name, List<JStructureSegment> children,
											 Option<List<Identifier>> typeParameters, Option<List<JDefinition>> params,
											 Option<JType> implementsClause) implements JStructure {
	}

	@Tag("struct")
	public record Structure(String name, List<CDefinition> fields, Option<String> after,
													Option<List<Identifier>> typeParameters) implements CRootSegment {
	}

	@Tag("whitespace")
	public record Whitespace() implements JavaRootSegment, JStructureSegment, JMethodSegment, CFunctionSegment {
	}

	@Tag("placeholder")
	public record Placeholder(String value) implements JMethodSegment, CFunctionSegment {
	}

	public record JavaRoot(List<JavaRootSegment> children) {
	}

	public record CRoot(List<CRootSegment> children) {
	}

	@Tag("import")
	public record Import(String location) implements JavaRootSegment {
	}

	@Tag("package")
	public record Package(String location) implements JavaRootSegment {
	}

	@Tag("definition")
	public record CDefinition(String name, CType type,
														Option<List<Identifier>> typeParameters) implements CParameter, CFunctionSegment {
	}

	@Tag("functionPointerDefinition")
	public record CFunctionPointerDefinition(String name, CType returnType, List<CType> paramTypes)
			implements CParameter {
	}

	@Tag("function")
	public record Function(CDefinition definition, List<CParameter> params, List<CFunctionSegment> body,
												 Option<String> after, Option<List<Identifier>> typeParameters) implements CRootSegment {
	}

	@Tag("identifier")
	public record Identifier(String value) implements JType, CType, JExpression, CExpression {
	}

	@Tag("pointer")
	public record Pointer(CType child) implements CType {
	}

	@Tag("functionPointer")
	public record FunctionPointer(CType returnType, List<CType> paramTypes) implements CType {
	}

	@Tag("line-comment")
	public record LineComment(String value) implements JStructureSegment, JMethodSegment, CFunctionSegment {
	}

	@Tag("block-comment")
	public record BlockComment(String value) implements JStructureSegment, JavaRootSegment {
	}

	@Tag("return")
	public record JReturn(JExpression value) implements JMethodSegment {
	}

	@Tag("return")
	public record CReturn(CExpression value) implements CFunctionSegment {
	}

	@Tag("else")
	public record JElse(JMethodSegment child) implements JMethodSegment {
	}

	@Tag("else")
	public record CElse(CFunctionSegment child) implements CFunctionSegment {
	}

	@Tag("invokable")
	public record JInvokable(JExpression caller, List<JExpression> arguments) implements JMethodSegment {
	}

	@Tag("invokable")
	public record CInvokable(CExpression caller, List<CExpression> arguments) implements CFunctionSegment {
	}

	@Tag("break")
	public record Break() implements JMethodSegment, CFunctionSegment {
	}

	public static Rule CRoot() {
		return Statements("children", Strip("", Or(CStructure(), Function()), "after"));
	}

	public static Rule Function() {
		final NodeRule definition = new NodeRule("definition", CDefinition());
		final Rule params = Arguments("params", Or(CFunctionPointerDefinition(), CDefinition()));
		final Rule body = Statements("body", CFunctionSegment());
		final Rule functionDecl =
				First(Suffix(First(definition, "(", params), ")"), " {", Suffix(body, System.lineSeparator() + "}"));

		// Add template declaration only if type parameters exist (non-empty list)
		final Rule templateParams = Arguments("typeParameters", Prefix("typename ", Identifier()));
		final Rule templateDecl =
				NonEmptyList("typeParameters", Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())));
		final Rule maybeTemplate = Or(templateDecl, Empty);

		return Tag("function", First(maybeTemplate, "", functionDecl));
	}

	private static Rule CFunctionPointerDefinition() {
		// Generates: returnType (*name)(paramTypes)
		return Tag("functionPointerDefinition",
				Suffix(First(Suffix(First(Node("returnType", CType()), " (*", String("name")), ")("),
						"",
						Arguments("paramTypes", CType())), ")"));
	}

	private static Rule CDefinition() {
		return Last(Node("type", CType()), " ", new StringRule("name"));
	}

	private static Rule CType() {
		final LazyRule rule = new LazyRule();
		// Function pointer: returnType (*)(paramType1, paramType2, ...)
		final Rule funcPtr =
				Tag("functionPointer", Suffix(First(Node("returnType", rule), " (*)(", Arguments("paramTypes", rule)), ")"));
		rule.set(Or(funcPtr, Identifier(), Tag("pointer", Suffix(Node("child", rule), "*")), Generic(rule)));
		return rule;
	}

	private static Rule CStructure() {
		// For template structs, use plain name without type parameters in the
		// declaration
		final Rule plainName = StrippedIdentifier("name");
		final Rule structPrefix = Prefix("struct ", plainName);
		final Rule fields = Statements("fields", Suffix(CDefinition(), ";"));
		final Rule structWithFields = Suffix(First(structPrefix, " {", fields), "}");
		final Rule structComplete = Suffix(structWithFields, ";");

		// Add template declaration only if type parameters exist (non-empty list)
		final Rule templateParams = Arguments("typeParameters", Prefix("typename ", Identifier()));
		final Rule templateDecl =
				NonEmptyList("typeParameters", Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())));
		final Rule maybeTemplate = Or(templateDecl, Empty);

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
		return Tag(type, Strip(Prefix(type + " ", Suffix(String("location"), ";"))));
	}

	private static Rule JStructure(String type, Rule rule) {
		final Rule modifiers = String("modifiers");

		final Rule maybeWithTypeArguments = NameWithTypeParameters();

		final Rule maybeWithParameters =
				Strip(Or(Suffix(First(maybeWithTypeArguments, "(", Parameters()), ")"), maybeWithTypeArguments));

		final Rule maybeWithParameters1 =
				Or(Last(maybeWithParameters, "extends", Node("extendsClause", JType())), maybeWithParameters);

		final Rule beforeContent =
				Or(Last(maybeWithParameters1, "implements", Node("implementsClause", JType())), maybeWithParameters1);

		final Rule children = Statements("children", rule);

		final Rule beforeContent1 = Or(Last(beforeContent, "permits", Delimited("variants", JType(), ",")), beforeContent);

		final Rule strip = Strip(Or(modifiers, Empty));
		final Rule first = First(strip, type + " ", beforeContent1);
		final Rule aClass = Split(first, new DividingSplitter(new FoldingDivider(new BraceStartFolder())), children);
		return Tag(type, Strip(Suffix(aClass, "}")));
	}

	private static Rule NameWithTypeParameters() {
		final Rule name = StrippedIdentifier("name");
		final Rule withTypeParameters = Suffix(First(name, "<", Arguments("typeParameters", Identifier())), ">");
		return Strip(Or(withTypeParameters, name));
	}

	private static Rule StructureSegment() {
		final LazyRule structureMember = new LazyRule();
		structureMember.set(Or(Structures(structureMember),
				Statement(),
				JMethod(),
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

	private static Rule JMethod() {
		Rule params = Parameters();
		final Rule header = Strip(Suffix(Last(Node("definition", JDefinition()), "(", params), ")"));
		final Rule withBody = Suffix(First(header, "{", Statements("body", JMethodSegment())), "}");
		return Tag("method", Strip(Or(Suffix(header, ";"), withBody)));
	}

	private static Rule JMethodSegment() {
		final LazyRule rule = new LazyRule();
		final Rule expression = JExpression();
		rule.set(Strip(Or(Whitespace(),
				LineComment(),
				Conditional("if", expression, rule),
				Conditional("while", expression, rule),
				Else(rule),
				Tag("try", Prefix("try ", Node("child", rule))),
				Strip(Suffix(JMethodStatementValue(), ";")),
				Block(rule))));
		return rule;
	}

	private static Rule Block(LazyRule rule) {
		return Tag("block", Strip(Prefix("{", Suffix(Statements("children", rule), "}"))));
	}

	private static Rule JMethodStatementValue() {
		final Rule expression = JExpression();
		return Or(Break(),
				Return(expression),
				Invokable(expression),
				Initialization(JDefinition(), expression),
				PostFix(expression),
				JDefinition());
	}

	private static Rule Break() {
		return Tag("break", Strip(Prefix("break", Empty)));
	}

	private static Rule PostFix(Rule expression) {
		return Tag("postFix", Strip(Suffix(Node("value", expression), "++")));
	}

	private static Rule Initialization(Rule definition, Rule value) {
		final Rule definition1 = Node("definition", definition);
		final Rule value1 = Node("value", value);
		return First(Or(Tag("initialization", definition1), Tag("assignment", Node("location", value))), "=", value1);
	}

	private static Rule Invokable(Rule expression) {
		return Or(Invokable("invocation", Node("caller", expression), expression),
				Invokable("construction", Node("type", CType()), expression));
	}

	private static Rule Invokable(String type, Rule caller, Rule expression) {
		final Rule arguments = Arguments("arguments", expression);
		return Tag(type, First(caller, "(", Suffix(Or(arguments, Whitespace()), ")")));
	}

	private static Rule Return(Rule expression) {
		return Tag("return", Prefix("return ", Node("value", expression)));
	}

	private static Rule Else(Rule statement) {
		return Tag("else", Prefix("else ", Node("child", statement)));
	}


	private static Rule Conditional(String tag, Rule expression, Rule statement) {
		final Rule condition = Node("condition", expression);
		final Rule body = Node("body", statement);
		final Rule split = Split(Prefix("(", condition),
				KeepFirst(new FoldingDivider(new EscapingFolder(new ClosingParenthesesFolder()))),
				body);
		return Tag(tag, Prefix(tag + " ", Strip(split)));
	}

	private static Rule JExpression() {
		final LazyRule expression = new LazyRule();
		expression.set(Or(
				StringExpr(),
				Switch(expression),
				Invokable(expression),
				Tag("field-access", Last(Node("child", expression), ".", Strip(String("name")))),
				Operator("add", "+", expression),
				Operator("equals", "==", expression),
				Identifier()));
		return expression;
	}

	private static Rule StringExpr() {
		return Tag("string", Strip(Prefix("\"", Suffix(Or(String("content"), Empty), "\""))));
	}

	private static Rule Operator(String type, String infix, LazyRule expression) {
		return Tag(type, First(Node("left", expression), infix, Node("right", expression)));
	}

	private static Rule Switch(LazyRule rule) {
		final Rule cases = Statements("cases", Strip(Or(Case(rule), Empty)));
		final Rule value = Prefix("(", Suffix(Node("value", rule), ")"));
		return Tag("switch", Strip(Prefix("switch ", Suffix(First(Strip(value), "{", cases), "}"))));
	}

	private static Rule Case(LazyRule rule) {
		return Prefix("case", Suffix(First(Node("definition", JDefinition()), "->", Node("value", rule)), ";"));
	}

	private static Rule CExpression() {
		return Or();
	}

	private static Rule CFunctionSegment() {
		final LazyRule rule = new LazyRule();
		rule.set(Or(Whitespace(), Prefix(System.lineSeparator() + "\t", CFunctionSegmentValue(rule)), Invalid()));
		return rule;
	}

	private static Rule Invalid() {
		return Tag("invalid", Placeholder(String("value")));
	}

	private static Rule CFunctionSegmentValue(LazyRule rule) {
		return Or(LineComment(),
				Conditional("if", CExpression(), rule),
				Conditional("while", CExpression(), rule),
				Break(),
				Else(rule),
				CFunctionStatement(),
				Block(rule));
	}

	private static Rule CFunctionStatement() {
		return Or(Suffix(CFunctionStatementValue(), ";"));
	}

	private static Rule CFunctionStatementValue() {
		final Rule expression = CExpression();
		return Or(Return(JExpression()),
				Invokable(expression),
				Initialization(CDefinition(), expression),
				CDefinition(),
				PostFix(expression));
	}

	private static Rule Parameters() {
		return Arguments("params", Or(ParameterDefinition(), Whitespace()));
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
		final Rule type = Node("type", JType());
		final Rule name = String("name");

		// Handle optional modifiers before type
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		final Rule withModifiers = Split(modifiers, KeepLast(new FoldingDivider(new TypeFolder())), type);

		Rule beforeName = Or(withModifiers, type);
		return Tag("definition", Strip(Last(beforeName, " ", name)));
	}

	private static Rule JType() {
		final LazyRule type = new LazyRule();
		type.set(Or(Generic(type), Array(type), Identifier(), Tag("wildcard", Strip(Prefix("?", Empty)))));
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
		final Rule base = Strip(String("base"));
		final Rule arguments = Or(Arguments("typeArguments", type), Strip(Empty));
		return Tag("generic", Strip(Suffix(First(base, "<", arguments), ">")));
	}
}
