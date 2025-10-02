package magma.compile;

import magma.compile.rule.ClosingParenthesesFolder;
import magma.compile.rule.DivideState;
import magma.compile.rule.DividingSplitter;
import magma.compile.rule.EscapingFolder;
import magma.compile.rule.FilterRule;
import magma.compile.rule.Folder;
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
	sealed public interface JavaRootSegment permits Invalid, Import, JStructure, Package, Whitespace, BlockComment {}

	sealed public interface CRootSegment permits Invalid, Structure, Function {
		Option<String> after();
	}

	public sealed interface JStructureSegment
			permits Invalid, JStructure, Method, Whitespace, Field, LineComment, BlockComment {}

	sealed public interface JExpression permits Invalid {}

	sealed public interface JMethodSegment
			permits Break, Invalid, JAssignment, JBlock, JElse, JIf, JInitialization, JInvokable, JPostFix, JReturn, JWhile,
			LineComment, Placeholder, Whitespace {}

	sealed public interface CFunctionSegment
			permits Break, CAssignment, CBlock, CElse, CIf, CInitialization, CInvokable, CPostFix, CReturn, CWhile, Invalid,
			LineComment, Placeholder, Whitespace {}

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

	public sealed interface CExpression permits Invalid {}

	@Tag("assignment")
	public record CAssignment(CExpression location, CExpression value) implements CFunctionSegment {}

	@Tag("postFix")
	public record CPostFix(CExpression value) implements CFunctionSegment {}

	@Tag("assignment")
	public record JAssignment(JExpression location, JExpression value) implements JMethodSegment {}

	@Tag("postFix")
	public record JPostFix(JExpression value) implements JMethodSegment {}

	@Tag("initialization")
	public record JInitialization(JDefinition definition, JExpression value) implements JMethodSegment {}

	@Tag("initialization")
	public record CInitialization(CDefinition definition, CExpression value) implements CFunctionSegment {}

	@Tag("block")
	public record CBlock(List<CFunctionSegment> children) implements CFunctionSegment {}

	@Tag("block")
	public record JBlock(List<JMethodSegment> children) implements JMethodSegment {}

	@Tag("if")
	public record JIf(JExpression condition, JMethodSegment body) implements JMethodSegment {}

	@Tag("if")
	public record CIf(CExpression condition, CFunctionSegment body) implements CFunctionSegment {}

	@Tag("while")
	public record JWhile(JExpression condition, JMethodSegment body) implements JMethodSegment {}

	@Tag("while")
	public record CWhile(CExpression condition, CFunctionSegment body) implements CFunctionSegment {}

	@Tag("statement")
	public record Field(JDefinition value) implements JStructureSegment {}

	@Tag("generic")
	public record Generic(String base, List<JavaType> arguments) implements JavaType, CType {}

	@Tag("array")
	public record Array(JavaType child) implements JavaType {}

	@Tag("definition")
	public record JDefinition(String name, JavaType type, Option<List<Modifier>> modifiers,
														Option<List<Identifier>> typeParameters) {}

	@Tag("modifier")
	public record Modifier(String value) {}

	@Tag("method")
	public record Method(JDefinition definition, Option<List<JDefinition>> params, Option<List<JMethodSegment>> body,
											 Option<List<Identifier>> typeParameters) implements JStructureSegment {}

	@Tag("invalid")
	public record Invalid(String value, Option<String> after)
			implements JavaRootSegment, JStructureSegment, CRootSegment, JavaType, CType, JMethodSegment, CFunctionSegment,
			JExpression, CExpression {}

	@Tag("class")
	public record JClass(Option<String> modifiers, String name, List<JStructureSegment> children,
											 Option<List<Identifier>> typeParameters, Option<JavaType> implementsClause)
			implements JStructure {}

	@Tag("interface")
	public record Interface(Option<String> modifiers, String name, List<JStructureSegment> children,
													Option<List<Identifier>> typeParameters, Option<JavaType> implementsClause,
													Option<JavaType> extendsClause, Option<List<JavaType>> variants) implements JStructure {}

	@Tag("record")
	public record Record(Option<String> modifiers, String name, List<JStructureSegment> children,
											 Option<List<Identifier>> typeParameters, Option<List<JDefinition>> params,
											 Option<JavaType> implementsClause) implements JStructure {}

	@Tag("struct")
	public record Structure(String name, List<CDefinition> fields, Option<String> after,
													Option<List<Identifier>> typeParameters) implements CRootSegment {}

	@Tag("whitespace")
	public record Whitespace() implements JavaRootSegment, JStructureSegment, JMethodSegment, CFunctionSegment {}

	@Tag("placeholder")
	public record Placeholder(String value) implements JMethodSegment, CFunctionSegment {}

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
	public record Function(CDefinition definition, List<CParameter> params, List<CFunctionSegment> body,
												 Option<String> after, Option<List<Identifier>> typeParameters) implements CRootSegment {}

	@Tag("identifier")
	public record Identifier(String value) implements JavaType, CType {}

	@Tag("pointer")
	public record Pointer(CType child) implements CType {}

	@Tag("functionPointer")
	public record FunctionPointer(CType returnType, List<CType> paramTypes) implements CType {}

	@Tag("line-comment")
	public record LineComment(String value) implements JStructureSegment, JMethodSegment, CFunctionSegment {}

	@Tag("block-comment")
	public record BlockComment(String value) implements JStructureSegment, JavaRootSegment {}

	@Tag("return")
	public record JReturn(JExpression value) implements JMethodSegment {}

	@Tag("return")
	public record CReturn(CExpression value) implements CFunctionSegment {}

	@Tag("else")
	public record JElse(JMethodSegment child) implements JMethodSegment {}

	@Tag("else")
	public record CElse(CFunctionSegment child) implements CFunctionSegment {}

	@Tag("invokable")
	public record JInvokable(JExpression caller, List<JExpression> arguments) implements JMethodSegment {}

	@Tag("invokable")
	public record CInvokable(CExpression caller, List<CExpression> arguments) implements CFunctionSegment {}

	@Tag("break")
	public record Break() implements JMethodSegment, CFunctionSegment {}

	public static Rule CRoot() {
		return Statements("children", Strip("", Or(CStructure(), Function(), Invalid()), "after"));
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
		rule.set(Or(funcPtr, Identifier(), Tag("pointer", Suffix(Node("child", rule), "*")), Generic(rule), Invalid()));
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
		return Tag(type, Strip(Prefix(type + " ", Suffix(Invalid(), ";"))));
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
		final Rule aClass = Split(first, new DividingSplitter(new FoldingDivider(new Folder() {
			@Override
			public DivideState fold(DivideState state, char c) {
				if (c == '{') {
					final DivideState entered = state.enter();
					if (entered.isShallow()) return entered.advance();
					return entered.append(c);
				}

				final DivideState state1 = state.append(c);
				if (c == '(') return state1.enter();
				if (c == '}' || c == ')') return state1.exit();
				return state1;
			}

			@Override
			public String delimiter() {
				return "";
			}
		})), children);
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
							PostFix(expression));
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
		return Tag("invokable", First(Node("caller", expression), "(", Arguments("arguments", expression)));
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
		return Invalid();
	}

	private static Rule CExpression() {
		return Or(Invalid());
	}

	private static Rule CFunctionSegment() {
		final LazyRule rule = new LazyRule();
		rule.set(Or(Whitespace(), Prefix(System.lineSeparator() + "\t", CFunctionSegmentValue(rule))));
		return rule;
	}

	private static Rule CFunctionSegmentValue(LazyRule rule) {
		return Or(LineComment(),
							Conditional("if", CExpression(), rule),
							Conditional("while", CExpression(), rule),
							Break(),
							Else(rule),
							CFunctionStatement(),
							Block(rule),
							Invalid());
	}

	private static Rule CFunctionStatement() {
		return Or(Suffix(CFunctionStatementValue(), ";"));
	}

	private static Rule CFunctionStatementValue() {
		final Rule expression = CExpression();
		return Or(Return(JExpression()),
							Invokable(expression),
							Initialization(CDefinition(), expression),
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
							 Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Arguments("arguments", type)), ">")));
	}

	private static Rule Invalid() {
		return Tag("invalid", Placeholder(String("value")));
	}
}
