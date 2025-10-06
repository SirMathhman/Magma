package magma.compile;

import magma.compile.rule.BraceStartFolder;
import magma.compile.rule.ClosingParenthesesFolder;
import magma.compile.rule.ContextRule;
import magma.compile.rule.DivideState;
import magma.compile.rule.DividingSplitter;
import magma.compile.rule.EscapingFolder;
import magma.compile.rule.FilterRule;
import magma.compile.rule.Folder;
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
import java.util.stream.Collectors;

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
			permits BlockComment, Field, Invalid, JDefinition, JInitialization, JStructure, LineComment, Method, Whitespace {}

	sealed public interface JExpression
			permits And, Cast, CharNode, Identifier, Index, InstanceOf, Invalid, JAdd, JConstruction, JEquals, JFieldAccess,
			JGreaterThan, JGreaterThanEquals, JInvocation, JLessThan, JLessThanEquals, JNotEquals, JOr, JString, JSubtract,
			Lambda, MethodAccess, NewArray, Not, Quantity, SwitchExpr {}

	sealed public interface JMethodSegment
			permits Break, Catch, Invalid, JAssignment, JBlock, JConstruction, JDefinition, JElse, JIf, JInitialization,
			JInvocation, JPostFix, JReturn, JWhile, LineComment, Placeholder, SwitchStatement, Try, Whitespace, Yield {}

	sealed public interface CFunctionSegment
			permits Break, CAssignment, CBlock, CDefinition, CElse, CIf, CInitialization, CInvocation, CPostFix, CReturn,
			CWhile, Invalid, LineComment, Placeholder, Whitespace {}

	sealed public interface JType extends InstanceOfTarget
			permits Array, Identifier, Invalid, JGeneric, Variadic, Wildcard {}

	sealed public interface CType {
		String stringify();
	}

	sealed public interface JStructure extends JavaRootSegment, JStructureSegment permits Interface, JClass, Record {
		String name();

		Option<List<Identifier>> typeParameters();

		List<JStructureSegment> children();
	}

	// Sealed interface for C parameter types
	public sealed interface CParameter permits CDefinition, CFunctionPointerDefinition {}

	public sealed interface CExpression
			permits CAdd, CAnd, CEquals, CFieldAccess, CInvocation, CString, CharNode, Identifier, Invalid {}

	public sealed interface InstanceOfTarget permits JDefinition, JType, Destruct {}

	public sealed interface CaseTarget permits JDefinition, Destruct {}

	public sealed interface CaseExprValue permits ExprCaseExprValue, StatementCaseExprValue {}

	public sealed interface LambdaValue permits ExprLambdaValue, StatementLambdaValue {}

	public sealed interface LambdaParamSet permits EmptyLambdaParam, MultipleLambdaParam, SingleLambdaParam {}

	public sealed interface MethodAccessSource permits ExprMethodAccessSource, TypeMethodAccessSource {}

	public sealed interface NewArrayValue {}

	@Tag("char")
	public record CharNode(String value) implements JExpression, CExpression {}

	@Tag("and")
	public record CAnd(CExpression left, CExpression right) implements CExpression {}

	@Tag("and")
	public record And(JExpression left, JExpression right) implements JExpression {}

	@Tag("destruct")
	public record Destruct(JType type, List<JDefinition> params) implements InstanceOfTarget, CaseTarget {}

	@Tag("instanceof")
	public record InstanceOf(JExpression child, InstanceOfTarget target) implements JExpression {}

	@Tag("wildcard")
	public record Wildcard() implements JType {}

	@Tag("add")
	public record JAdd(JExpression left, JExpression right) implements JExpression {}

	@Tag("subtract")
	public record JSubtract(JExpression left, JExpression right) implements JExpression {}

	@Tag("equals")
	public record JEquals(JExpression left, JExpression right) implements JExpression {}

	@Tag("not-equals")
	public record JNotEquals(JExpression left, JExpression right) implements JExpression {}

	@Tag("equals")
	public record CEquals(CExpression left, CExpression right) implements CExpression {}

	@Tag("string")
	public record JString(Option<String> content) implements JExpression {}

	@Tag("add")
	public record CAdd(CExpression left, CExpression right) implements CExpression {}

	@Tag("string")
	public record CString(String content) implements CExpression {}

	@Tag("field-access")
	public record JFieldAccess(JExpression child, String name) implements JExpression {}

	@Tag("field-access")
	public record CFieldAccess(CExpression child, String name) implements CExpression {}

	@Tag("construction")
	public record JConstruction(JType type, Option<List<JExpression>> arguments) implements JExpression, JMethodSegment {}

	@Tag("invocation")
	public record JInvocation(JExpression caller, Option<List<JExpression>> arguments)
			implements JExpression, JMethodSegment {}

	@Tag("not")
	public record Not(JExpression child) implements JExpression {}

	@Tag("expr-case-expr-value")
	public record ExprCaseExprValue(JExpression expression) implements CaseExprValue {}

	@Tag("statement-case-expr-value")
	public record StatementCaseExprValue(JMethodSegment statement) implements CaseExprValue {}

	@Tag("case-expr")
	public record CaseExpr(Option<CaseTarget> target, CaseExprValue value) {}

	@Tag("case-statement")
	public record CaseStatement(Option<CaseTarget> target, JMethodSegment value) {}

	@Tag("switch-expr")
	public record SwitchExpr(JExpression value, List<CaseExpr> cases) implements JExpression {}

	@Tag("switch-statement")
	public record SwitchStatement(JExpression value, List<CaseStatement> cases) implements JMethodSegment {}

	@Tag("expr-lambda-value")
	public record ExprLambdaValue(JExpression child) implements LambdaValue {}

	@Tag("statement-lambda-value")
	public record StatementLambdaValue(JMethodSegment child) implements LambdaValue {}

	@Tag("lambda")
	public record Lambda(LambdaParamSet params, LambdaValue child) implements JExpression {}

	@Tag("length")
	public record LengthNewArrayValue(JExpression length) implements NewArrayValue {}

	@Tag("arguments")
	public record ArgumentsNewArrayValue(Option<List<JExpression>> arguments) implements NewArrayValue {}

	@Tag("new-array")
	public record NewArray(JType type, NewArrayValue value) implements JExpression {}

	@Tag("assignment")
	public record CAssignment(CExpression location, CExpression value) implements CFunctionSegment {}

	@Tag("postFix")
	public record CPostFix(CExpression value) implements CFunctionSegment {}

	@Tag("assignment")
	public record JAssignment(JExpression location, JExpression value) implements JMethodSegment {}

	@Tag("postFix")
	public record JPostFix(JExpression value) implements JMethodSegment {}

	@Tag("initialization")
	public record JInitialization(JDefinition definition, JExpression value)
			implements JMethodSegment, JStructureSegment {}

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
	public record JGeneric(String base, Option<List<JType>> typeArguments) implements JType {}

	@Tag("generic")
	public record CGeneric(String base, List<CType> typeArguments) implements CType {
		@Override
		public String stringify() {
			return base + "_" + typeArguments.stream().map(CType::stringify).collect(Collectors.joining("_"));
		}
	}

	@Tag("array")
	public record Array(JType child) implements JType {}

	@Tag("definition")
	public record JDefinition(String name, JType type, Option<List<Modifier>> modifiers,
														Option<List<Identifier>> typeParameters)
			implements JMethodSegment, InstanceOfTarget, JStructureSegment, CaseTarget {}

	@Tag("modifier")
	public record Modifier(String value) {}

	@Tag("method")
	public record Method(JDefinition definition, Option<List<JDefinition>> params, Option<List<JMethodSegment>> body,
											 Option<List<Identifier>> typeParameters) implements JStructureSegment {}

	@Tag("invalid")
	public record Invalid(String value, Option<String> after)
			implements JavaRootSegment, JStructureSegment, CRootSegment, JType, CType, JMethodSegment, CFunctionSegment,
			JExpression, CExpression {
		public Invalid(String value) {
			this(value, new None<>());
		}

		@Override
		public String stringify() {
			return "???";
		}
	}

	@Tag("class")
	public record JClass(Option<String> modifiers, String name, List<JStructureSegment> children,
											 Option<List<Identifier>> typeParameters, Option<List<JType>> interfaces) implements JStructure {}

	@Tag("interface")
	public record Interface(Option<String> modifiers, String name, List<JStructureSegment> children,
													Option<List<Identifier>> typeParameters, Option<List<JType>> interfaces,
													Option<List<JType>> superclasses, Option<List<JType>> variants) implements JStructure {}

	@Tag("record")
	public record Record(Option<String> modifiers, String name, List<JStructureSegment> children,
											 Option<List<Identifier>> typeParameters, Option<List<JDefinition>> params,
											 Option<List<JType>> interfaces) implements JStructure {}

	@Tag("struct")
	public record Structure(String name, List<CDefinition> fields, Option<String> after,
													Option<List<Identifier>> typeParameters) implements CRootSegment {}

	@Tag("whitespace")
	public record Whitespace() implements JavaRootSegment, JStructureSegment, JMethodSegment, CFunctionSegment {}

	@Tag("placeholder")
	public record Placeholder(String value) implements JMethodSegment, CFunctionSegment {}

	public record JRoot(List<JavaRootSegment> children) {}

	public record CRoot(List<CRootSegment> children) {}

	@Tag("import")
	public record Import(String location) implements JavaRootSegment {}

	@Tag("package")
	public record Package(String location) implements JavaRootSegment {}

	@Tag("definition")
	public record CDefinition(String name, CType type, Option<List<Identifier>> typeParameters)
			implements CParameter, CFunctionSegment {}

	@Tag("functionPointerDefinition")
	public record CFunctionPointerDefinition(String name, CType returnType, List<CType> paramTypes)
			implements CParameter {}

	@Tag("function")
	public record Function(CDefinition definition, List<CParameter> params, List<CFunctionSegment> body,
												 Option<String> after, Option<List<Identifier>> typeParameters) implements CRootSegment {}

	@Tag("identifier")
	public record Identifier(String value) implements JType, CType, JExpression, CExpression {
		@Override
		public String stringify() {
			return value;
		}
	}

	@Tag("pointer")
	public record Pointer(CType child) implements CType {
		@Override
		public String stringify() {
			return child.stringify() + "_ref";
		}
	}

	@Tag("functionPointer")
	public record FunctionPointer(CType returnType, List<CType> paramTypes) implements CType {
		@Override
		public String stringify() {
			return "fn_" + paramTypes.stream().map(CType::stringify).collect(Collectors.joining("_")) + "_" +
						 returnType.stringify();
		}
	}

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

	@Tag("invocation")
	public record CInvocation(CExpression caller, List<CExpression> arguments) implements CFunctionSegment, CExpression {}

	@Tag("break")
	public record Break() implements JMethodSegment, CFunctionSegment {}

	private record InvocationFolder(char open, char close) implements Folder {
		@Override
		public DivideState fold(DivideState state, char c) {
			DivideState appended = state.append(c);
			if (c == open) {
				DivideState enter = appended.enter();
				if (enter.isShallow()) return enter.advance();
				else return enter;
			}
			if (c == close) return appended.exit();
			return appended;
		}

		@Override
		public String delimiter() {
			return "";
		}
	}

	@Tag("index")
	public record Index(JExpression child, JExpression index) implements JExpression {}

	@Tag("quantity")
	public record Quantity(JExpression child) implements JExpression {}

	@Tag("cast")
	public record Cast(JType type, JExpression child) implements JExpression {}

	@Tag("less-than-equals")
	public record JLessThanEquals(JExpression left, JExpression right) implements JExpression {}

	@Tag("greater-than")
	public record JGreaterThan(JExpression left, JExpression right) implements JExpression {}

	@Tag("or")
	public record JOr(JExpression left, JExpression right) implements JExpression {}

	@Tag("greater-than-equals")
	public record JGreaterThanEquals(JExpression left, JExpression right) implements JExpression {}

	@Tag("less-than")
	public record JLessThan(JExpression left, JExpression right) implements JExpression {}

	@Tag("try")
	public record Try(JMethodSegment child) implements JMethodSegment {}

	@Tag("catch")
	public record Catch(JDefinition definition, JMethodSegment body) implements JMethodSegment {}

	@Tag("yield")
	public record Yield(JExpression child) implements JMethodSegment {}

	@Tag("variadic")
	public record Variadic(JType child) implements JType {}

	private static class MyFolder implements Folder {
		@Override
		public DivideState fold(DivideState state, char c) {
			if (c == '(') return state.append(c).enter();
			if (c == ')') if (state.isLevel()) return state.advance();
			else return state.exit().append(c);
			return state.append(c);
		}

		@Override
		public String delimiter() {
			return "";
		}
	}

	@Tag("none")
	public record EmptyLambdaParam() implements LambdaParamSet {}

	@Tag("single")
	public record SingleLambdaParam(String param) implements LambdaParamSet {}

	@Tag("multiple")
	public record MultipleLambdaParam(Option<List<SingleLambdaParam>> params) implements LambdaParamSet {}

	@Tag("expr-method-access-source")
	public record ExprMethodAccessSource(JExpression child) implements MethodAccessSource {}

	@Tag("type-method-access-source")
	public record TypeMethodAccessSource(JType child) implements MethodAccessSource {}

	@Tag("method-access")
	public record MethodAccess(String name, MethodAccessSource source) implements JExpression {}

	public static Rule CRoot() {
		return Statements("children", Strip("", Or(CStructure(), Function()), "after"));
	}

	public static Rule Function() {
		final NodeRule definition = new NodeRule("definition", CDefinition());
		final Rule params = Expressions("params", Or(CFunctionPointerDefinition(), CDefinition()));
		final Rule body = Statements("body", CFunctionSegment());
		final Rule first = First(definition, "(", params);
		final Rule suffix = Suffix(first, ")");
		final Rule suffix1 = Suffix(body, System.lineSeparator() + "}");
		final Rule functionDecl = First(suffix, " {", suffix1);

		// Add template declaration only if type parameters exist (non-empty list)
		final Rule templateParams = Expressions("typeParameters", Prefix("typename ", Identifier()));
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
														Expressions("paramTypes", CType())), ")"));
	}

	private static Rule CDefinition() {
		return Last(Node("type", CType()), " ", new StringRule("name"));
	}

	private static Rule CType() {
		final LazyRule rule = new LazyRule();
		// Function pointer: returnType (*)(paramType1, paramType2, ...)
		final Rule funcPtr =
				Tag("functionPointer", Suffix(First(Node("returnType", rule), " (*)(", Expressions("paramTypes", rule)), ")"));
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
		final Rule templateParams = Expressions("typeParameters", Prefix("typename ", Identifier()));
		final Rule templateDecl =
				NonEmptyList("typeParameters", Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())));
		final Rule maybeTemplate = Or(templateDecl, Empty);

		return Tag("struct", First(maybeTemplate, "", structComplete));
	}

	public static Rule JRoot() {
		final Rule segment =
				Or(Namespace("package"), Namespace("import"), Structures(JStructureSegment()), BlockComment(), Whitespace());
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
				Or(Last(maybeWithParameters, "extends", Expressions("superclasses", JType())), maybeWithParameters);

		final Rule beforeContent =
				Or(Last(maybeWithParameters1, "implements", Expressions("interfaces", JType())), maybeWithParameters1);

		final Rule children = Statements("children", rule);

		final Rule beforeContent1 = Or(Last(beforeContent, "permits", Delimited("variants", JType(), ",")), beforeContent);

		final Rule strip = Strip(Or(modifiers, Empty));
		final Rule first = First(strip, type + " ", beforeContent1);
		final Rule aClass = Split(first, new DividingSplitter(new FoldingDivider(new BraceStartFolder())), children);
		return Tag(type, Strip(Suffix(aClass, "}")));
	}

	private static Rule NameWithTypeParameters() {
		final Rule name = StrippedIdentifier("name");
		final Rule withTypeParameters = Suffix(First(name, "<", Expressions("typeParameters", Identifier())), ">");
		return Strip(Or(withTypeParameters, name));
	}

	private static Rule JStructureSegment() {
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
		final Rule initialization = Initialization(JDefinition(), JExpression(JMethodSegment()));
		return Strip(Suffix(Or(initialization, JDefinition()), ";"));
	}

	private static Rule JMethod() {
		Rule params = Parameters();
		final Rule header = Strip(Suffix(Last(Node("definition", JDefinition()), "(", params), ")"));
		final Rule withBody = Suffix(First(header, "{", Statements("body", JMethodSegment())), "}");
		return Tag("method", Strip(Or(Suffix(header, ";"), withBody)));
	}

	private static Rule JMethodSegment() {
		final LazyRule methodSegment = new LazyRule();
		final Rule expression = JExpression(methodSegment);
		Rule inner = JDefinition();
		methodSegment.set(Strip(Or(Whitespace(),
															 LineComment(),
															 Switch("statement", expression, methodSegment),
															 Conditional("if", expression, methodSegment),
															 Conditional("while", expression, methodSegment),
															 Else(methodSegment),
															 Try(methodSegment),
															 QuantityBlock("catch", "definition", inner, methodSegment),
															 Strip(Suffix(JMethodStatementValue(methodSegment), ";")),
															 Block(methodSegment),
															 BlockComment())));
		return methodSegment;
	}

	private static Rule Try(LazyRule methodSegment) {
		final Rule child = Node("child", methodSegment);
		final Rule resource = Node("resource", Initialization(JDefinition(), JExpression(methodSegment)));
		final Splitter splitter = new DividingSplitter(new FoldingDivider(new EscapingFolder(new MyFolder())));
		final Rule withResource =
				new ContextRule("With resource", Strip(Prefix("(", new SplitRule(resource, child, splitter))));
		final ContextRule withoutResource = new ContextRule("Without resource", child);
		return Tag("try", Prefix("try ", Or(withResource, withoutResource)));
	}

	private static Rule Block(LazyRule rule) {
		return Tag("block", Strip(Prefix("{", Suffix(Statements("children", rule), "}"))));
	}

	private static Rule JMethodStatementValue(Rule statement) {
		final Rule expression = JExpression(statement);
		return Or(Break(),
							PostFix(expression),
							Return(expression),
							Yield(expression),
							Initialization(JDefinition(), expression),
							JDefinition(),
							Invokable(expression));
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
		return Or(Invocation(expression),
							Invokable("construction", Strip(Prefix("new ", Node("type", CType()))), expression));
	}

	private static Rule Invokable(String type, Rule caller, Rule expression) {
		final Rule arguments = Expressions("arguments", expression);
		FoldingDivider divider = new FoldingDivider(new EscapingFolder(new InvocationFolder('(', ')')));
		final Rule suffix = Strip(Suffix(Or(arguments, Whitespace()), String.valueOf(')')));
		return Tag(type, Split(Suffix(caller, String.valueOf('(')), KeepLast(divider), suffix));
	}

	private static Rule Yield(Rule expression) {
		return Tag("yield", Prefix("yield ", Node("child", expression)));
	}

	private static Rule Return(Rule expression) {
		return Tag("return", Prefix("return ", Node("value", expression)));
	}

	private static Rule Else(Rule statement) {
		return Tag("else", Prefix("else", Node("child", statement)));
	}

	private static Rule Conditional(String tag, Rule inner, Rule statement) {
		return QuantityBlock(tag, "condition", inner, statement);
	}

	private static Rule QuantityBlock(String tag, String key, Rule inner, Rule statement) {
		final Rule condition = Node(key, inner);
		final Rule body = Node("body", statement);
		final Rule split = Split(Prefix("(", condition),
														 KeepFirst(new FoldingDivider(new EscapingFolder(new ClosingParenthesesFolder()))),
														 body);

		return Tag(tag, Prefix(tag + " ", Strip(split)));
	}

	private static Rule JExpression(Rule statement) {
		final LazyRule expression = new LazyRule();
		expression.set(Or(Lambda(statement, expression),
											Char(),
											Tag("cast", Strip(Prefix("(", First(Node("type", JType()), ")", Node("child", expression))))),
											Tag("quantity", Strip(Prefix("(", Suffix(Node("child", expression), ")")))),
											Tag("not", Strip(Prefix("!", Node("child", expression)))),
											StringExpr(),
											Switch("expr", expression, CaseExprValue(statement, expression)),
											Index(expression),
											NewArray(expression),
											Index(expression),
											Invokable(expression),
											FieldAccess(expression),
											MethodAccess(expression),
											InstanceOf(expression),
											Operator("add", "+", expression),
											Operator("subtract", "-", expression),
											Operator("and", "&&", expression),
											Operator("or", "||", expression),
											Operator("equals", "==", expression),
											Operator("not-equals", "!=", expression),
											Operator("less-than", "<", expression),
											Operator("less-than-equals", "<=", expression),
											Operator("greater-than", ">", expression),
											Operator("greater-than-equals", ">=", expression),
											Identifier()));
		return expression;
	}

	private static Rule NewArray(LazyRule expression) {
		final Rule type = Node("type", JType());

		final Rule tag = Tag("arguments", Suffix(Expressions("arguments", expression), "}"));
		final Rule tag1 = Tag("length", Node("length", expression));

		final Rule withoutArguments = Suffix(First(type, "[", Node("value", tag1)), "]");
		final Rule withArguments = Strip(First(type, "[]{", Node("value", tag)));

		return Tag("new-array", Strip(Prefix("new ", Or(withoutArguments, withArguments))));
	}

	private static Rule MethodAccess(LazyRule expression) {
		final Rule exprSource = Tag("expr-method-access-source", Node("child", expression));
		final Rule child = Tag("type-method-access-source", Node("child", JType()));
		return Tag("method-access", Last(Node("source", Or(exprSource, child)), "::", StrippedIdentifier("name")));
	}

	private static Rule CaseExprValue(Rule statement, LazyRule expression) {
		return Or(Tag("expr-case-expr-value", Node("expression", Strip(Suffix(expression, ";")))),
							Tag("statement-case-expr-value", Node("statement", statement)));
	}

	private static Rule Char() {
		return Tag("char", Strip(Prefix("'", Suffix(String("value"), "'"))));
	}

	private static Rule Lambda(Rule statement, Rule expression) {
		final Rule param = Tag("single", StrippedIdentifier("param"));
		final Rule expressions = Tag("multiple", Expressions("params", StrippedIdentifier("param")));
		final Rule tag = Tag("none", Empty);

		final Rule strip = Or(Strip(Prefix("(", Suffix(Or(expressions, tag), ")"))), param);
		final Rule child = Node("child",
														Or(Tag("statement-lambda-value", Node("child", statement)),
															 Tag("expr-lambda-value", Node("child", expression))));

		return Tag("lambda", First(Node("params", strip), "->", child));
	}

	private static Rule InstanceOf(LazyRule expression) {
		final Rule strip = Destruct();
		Rule type = Node("target", Or(JDefinition(), JType(), strip));
		return Tag("instanceof", Last(Node("child", expression), "instanceof", type));
	}

	private static Rule Destruct() {
		return Tag("destruct", Strip(Suffix(First(Node("type", JType()), "(", Parameters()), ")")));
	}

	private static Rule Index(LazyRule expression) {
		return Tag("index",
							 Strip(Suffix(Last(new NodeRule("child", expression), "[", new NodeRule("index", expression)), "]")));
	}

	private static Rule FieldAccess(Rule expression) {
		return Tag("field-access", Last(Node("child", expression), ".", StrippedIdentifier("name")));
	}

	private static Rule StringExpr() {
		return Tag("string", Strip(Prefix("\"", Suffix(Or(String("content"), Empty), "\""))));
	}

	private static Rule Operator(String type, String infix, LazyRule expression) {
		return Tag(type, First(Node("left", expression), infix, Node("right", expression)));
	}

	private static Rule Switch(String group, Rule expression, Rule rule) {
		final Rule cases = Statements("cases", Strip(Or(Case(group, rule), Empty)));
		final Rule value = Prefix("(", Suffix(Node("value", expression), ")"));
		return Tag("switch-" + group, Strip(Prefix("switch ", Suffix(First(Strip(value), "{", cases), "}"))));
	}

	private static Rule Case(String group, Rule rule) {
		Rule after = Node("target", Or(JDefinition(), Destruct()));
		final Rule defaultCase = Strip(Prefix("default", Empty));
		Rule value = First(Or(defaultCase, Prefix("case", after)), "->", Node("value", rule));
		return Tag("case-" + group, value);
	}

	private static Rule CExpression() {
		LazyRule expression = new LazyRule();
		expression.set(Or(Invocation(expression),
											FieldAccess(expression),
											Operator("add", "+", expression),
											Operator("and", "&&", expression),
											Operator("equals", "==", expression),
											StringExpr(),
											Identifier(),
											Char(),
											Invalid()));
		return expression;
	}

	private static Rule Invocation(Rule expression) {
		return Invokable("invocation", Node("caller", expression), expression);
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
		LazyRule functionStatement = new LazyRule();
		functionStatement.set(Or(Conditional("if", CExpression(), functionStatement),
														 Suffix(CFunctionStatementValue(), ";")));
		return functionStatement;
	}

	private static Rule CFunctionStatementValue() {
		final Rule expression = CExpression();
		return Or(Return(expression),
							Invocation(expression),
							Initialization(CDefinition(), expression),
							CDefinition(),
							PostFix(expression));
	}

	private static Rule Parameters() {
		return Expressions("params", Or(JDefinition(), Whitespace()));
	}

	private static Rule JDefinition() {
		// Use TypeFolder to properly parse generic types like Function<T, R>
		// Split into modifiers+type and name using type-aware splitting
		final Rule type = Node("type", JType());
		final Rule name = StrippedIdentifier("name");

		// Handle optional modifiers before type
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		final Rule withModifiers = Split(modifiers, KeepLast(new FoldingDivider(new TypeFolder())), type);

		Rule beforeName = Or(withModifiers, type);
		return Tag("definition", Strip(Last(beforeName, " ", name)));
	}

	private static Rule JType() {
		final LazyRule type = new LazyRule();
		type.set(Or(Generic(type),
								Array(type),
								Identifier(),
								WildCard(),
								Tag("variadic", Strip(Suffix(Node("child", type), "...")))));
		return type;
	}

	private static Rule WildCard() {
		return Tag("wildcard", Strip(Prefix("?", Empty)));
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
		final Rule base = StrippedIdentifier("base");
		final Rule arguments = Or(Expressions("typeArguments", type), Strip(Empty));
		return Tag("generic", Strip(Suffix(First(base, "<", arguments), ">")));
	}
}
