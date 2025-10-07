// Generated transpiled C++ from 'src\main\java\magma\compile\Lang.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Lang {};
struct JavaRootSegment {};
struct CRootSegment {};
Option<String> after_CRootSegment() {
}
struct JStructureSegment {};
struct JExpression {};
struct JMethodSegment {};
struct CFunctionSegment {};
struct JType {};
struct JStructure {};
String name_JStructure() {
}
Option<List<Identifier>> typeParameters_JStructure() {
}
List<JStructureSegment> children_JStructure() {
}
struct CParameter {};
struct CExpression {};
struct InstanceOfTarget {};
struct CaseTarget {};
struct CaseExprValue {};
struct LambdaValue {};
struct LambdaParamSet {};
struct MethodAccessSource {};
struct NewArrayValue {};
struct Identifiable {};
struct CharNode {String value;};
struct CAnd {CExpression left;CExpression right;};
struct And {JExpression left;JExpression right;};
struct Destruct {JType type;List<JDefinition> params;};
struct InstanceOf {JExpression child;InstanceOfTarget target;};
struct Wildcard {};
struct JAdd {JExpression left;JExpression right;};
struct JSubtract {JExpression left;JExpression right;};
struct JEquals {JExpression left;JExpression right;};
struct JNotEquals {JExpression left;JExpression right;};
struct CEquals {CExpression left;CExpression right;};
struct JString {Option<String> content;};
struct CAdd {CExpression left;CExpression right;};
struct CString {String content;};
struct JFieldAccess {JExpression child;String name;};
struct CFieldAccess {CExpression child;String name;};
struct JConstruction {JType type;Option<List<JExpression>> arguments;};
struct JInvocation {JExpression caller;Option<List<JExpression>> arguments;};
struct Not {JExpression child;};
struct ExprCaseExprValue {JExpression expression;};
struct StatementCaseExprValue {JMethodSegment statement;};
record CaseExpr_Lang(Option<CaseTarget> target, CaseExprValue value, Option<JExpression> when) {
}
record CaseStatement_Lang(Option<CaseTarget> target, JMethodSegment value, Option<JExpression> when) {
}
struct SwitchExpr {JExpression value;List<CaseExpr> cases;};
struct SwitchStatement {JExpression value;List<CaseStatement> cases;};
struct ExprLambdaValue {JExpression child;};
struct StatementLambdaValue {JMethodSegment child;};
struct Lambda {LambdaParamSet params;LambdaValue child;};
struct LengthNewArrayValue {JExpression length;};
struct ArgumentsNewArrayValue {Option<List<JExpression>> arguments;};
struct NewArray {JType type;NewArrayValue value;};
struct CAssignment {CExpression location;CExpression value;};
struct CPostFix {CExpression value;};
struct JAssignment {JExpression location;JExpression value;};
struct JPostFix {JExpression value;};
struct JInitialization {JDefinition definition;JExpression value;};
struct CInitialization {CDefinition definition;CExpression value;};
struct CBlock {List<CFunctionSegment> children;};
struct JBlock {List<JMethodSegment> children;};
struct JIf {JExpression condition;JMethodSegment body;};
struct CIf {CExpression condition;CFunctionSegment body;};
struct JWhile {JExpression condition;JMethodSegment body;};
struct CWhile {CExpression condition;CFunctionSegment body;};
struct Field {JDefinition value;};
struct JGeneric {JQualified base;Option<List<JType>> typeArguments;};
struct CTemplate {String base;NonEmptyList<CType> typeArguments;};
String stringify_CTemplate() {
	return base+""+typeArguments.stream().map(/*???*/).collect(new_???(""));
}
struct Array {JType child;};
struct JDefinition {String name;JType type;Option<List<Modifier>> modifiers;Option<List<Identifier>> typeParameters;};
record Modifier_Lang(String value) {
}
struct Method {JDefinition definition;Option<List<JDefinition>> params;Option<List<JMethodSegment>> body;Option<List<Identifier>> typeParameters;};
struct Invalid {String value;Option<String> after;};
public Invalid_Invalid(String value) {
	this(value, new_???());
}
String stringify_Invalid() {
	return "";
}
struct JClass {Option<String> modifiers;String name;List<JStructureSegment> children;Option<List<Identifier>> typeParameters;Option<List<JType>> interfaces;};
struct Interface {Option<String> modifiers;String name;List<JStructureSegment> children;Option<List<Identifier>> typeParameters;Option<List<JType>> interfaces;Option<List<JType>> superclasses;Option<List<JType>> variants;};
struct RecordNode {Option<String> modifiers;String name;List<JStructureSegment> children;Option<List<Identifier>> typeParameters;Option<List<JDefinition>> params;Option<List<JType>> interfaces;};
struct Structure {String name;List<CDefinition> fields;Option<String> after;Option<List<Identifier>> typeParameters;};
struct Whitespace {};
struct Placeholder {String value;};
record JRoot_Lang(List<JavaRootSegment> children) {
}
record CRoot_Lang(List<CRootSegment> children) {
}
struct Import {String location;};
struct Package {String location;};
struct CDefinition {String name;CType type;Option<List<Identifier>> typeParameters;};
struct CFunctionPointerDefinition {String name;CType returnType;List<CType> paramTypes;};
struct Function {CDefinition definition;List<CParameter> params;List<CFunctionSegment> body;Option<String> after;Option<List<Identifier>> typeParameters;};
struct Identifier {String value;};
String stringify_Identifier() {
	return value;
}
struct Pointer {CType child;};
String stringify_Pointer() {
	return child.stringify()+"";
}
struct LineComment {String value;};
struct BlockComment {String value;};
struct JReturn {JExpression value;};
struct CReturn {CExpression value;};
struct JElse {JMethodSegment child;};
struct CElse {CFunctionSegment child;};
struct CInvocation {CExpression caller;List<CExpression> arguments;};
struct Break {};
struct InvocationFolder {char open;char close;};
DivideState fold_InvocationFolder(DivideState state, char c) {
	DivideState appended=state.append(c);
	if (c==open)
	{
	DivideState enter=appended.enter();
	if (enter.isShallow())return enter.advance();
	return enter;}
	if (c==close)return appended.exit();
	return appended;
}
String delimiter_InvocationFolder() {
	return "";
}
struct Index {JExpression child;JExpression index;};
struct Quantity {JExpression child;};
struct Cast {JType type;JExpression child;};
struct JLessThanEquals {JExpression left;JExpression right;};
struct JGreaterThan {JExpression left;JExpression right;};
struct JOr {JExpression left;JExpression right;};
struct JGreaterThanEquals {JExpression left;JExpression right;};
struct JLessThan {JExpression left;JExpression right;};
struct Try {JMethodSegment child;Option<JInitialization> resource;};
struct Catch {JDefinition definition;JMethodSegment body;};
struct Yield {JExpression child;};
struct Variadic {JType child;};
struct MyFolder {};
DivideState fold_MyFolder(DivideState state, char c) {
	if (c=='(')return state.append(c).enter();
	if (c==')')if (state.isLevel())return state.advance();
	else
	return state.exit().append(c);
	return state.append(c);
}
String delimiter_MyFolder() {
	return "";
}
struct EmptyLambdaParam {};
struct SingleLambdaParam {String param;};
struct MultipleLambdaParam {Option<List<SingleLambdaParam>> params;};
struct ExprMethodAccessSource {JExpression child;};
struct TypeMethodAccessSource {JType child;};
struct MethodAccess {String name;MethodAccessSource source;};
struct NumberNode {String number;};
struct OperatorFolder {String operator;};
DivideState fold_OperatorFolder(DivideState state, char c) {
	if (c==operator.charAt(/*???*/))
	{
	if (/*???*/)
	{
	state.pop();
	return state.advance();}
	return state.advance();}
	return state.append(c);
}
String delimiter_OperatorFolder() {
	return operator;
}
record QualifiedSegment_Lang(String value) {
}
struct JQualified {Option<List<QualifiedSegment>> segments;};
String last_JQualified() {
	if (/*???*/)return "";
	return segments.orElse(new_???()).getLast().map(/*???*/.value).orElse("");
}
boolean endsWith_JQualified(String name) {
	return /*???*/;
}
List<String> unwrap_JQualified() {
	if (/*???*/)return list.stream().map(/*???*/.value).toList();
	return new_???();
}
Rule CFunctionPointerDefinition_Lang() {
	return Tag("", Suffix(First(Suffix(First(Node("", CType()), "", String("")), ""), "", Expressions("", CType())), ""));
}
Rule CDefinition_Lang() {
	return Last(Node("", CType()), "", new_???(""));
}
Rule CType_Lang() {
	LazyRule rule=new_???();
	Rule funcPtr=Tag("", Suffix(First(Node("", rule), "", Expressions("", rule)), ""));
	rule.set(Or(funcPtr, CommonRules.Identifier(), Tag("", Suffix(Node("", rule), "")), CTemplate(rule), Invalid()));
	return rule;
}
Rule CTemplate_Lang(LazyRule rule) {
	Rule base=String("");
	Rule arguments=Or(Expressions("", rule));
	return Tag("", Strip(Suffix(First(base, "", arguments), "")));
}
Rule CStructure_Lang() {
	Rule plainName=CommonRules.StrippedIdentifier("");
	Rule structPrefix=Prefix("", plainName);
	Rule fields=Statements("", Suffix(CDefinition(), ""));
	Rule structWithFields=Suffix(First(structPrefix, "", fields), "");
	Rule structComplete=Suffix(structWithFields, "");
	Rule templateParams=Expressions("", Prefix("", CommonRules.Identifier()));
	Rule templateDecl=NonEmptyList("", Prefix("", Suffix(templateParams, ""+System.lineSeparator())));
	Rule maybeTemplate=Or(templateDecl, Empty);
	return Tag("", First(maybeTemplate, "", structComplete));
}
Rule JRoot_Lang() {
	Rule segment=Or(Namespace(""), Namespace(""), Structures(JStructureSegment()), BlockComment(), Whitespace());
	return Statements("", segment);
}
Rule Structures_Lang(Rule structureMember) {
	return Or(JStructure("", structureMember), JStructure("", structureMember), JStructure("", structureMember));
}
Rule Whitespace_Lang() {
	return Tag("", Strip(Empty));
}
Rule Namespace_Lang(String type) {
	return Tag(type, Strip(Prefix(type+"", Suffix(String(""), ""))));
}
Rule JStructure_Lang(String type, Rule rule) {
	Rule modifiers=String("");
	Rule maybeWithTypeArguments=NameWithTypeParameters();
	Rule maybeWithParameters=Strip(Or(Suffix(First(maybeWithTypeArguments, "", Parameters()), ""), maybeWithTypeArguments));
	Rule maybeWithParameters1=Or(Last(maybeWithParameters, "", Expressions("", JRules.JType())), maybeWithParameters);
	Rule beforeContent=Or(Last(maybeWithParameters1, "", Expressions("", JRules.JType())), maybeWithParameters1);
	Rule children=Statements("", rule);
	Rule beforeContent1=Or(Last(beforeContent, "", Delimited("", JRules.JType(), "")), beforeContent);
	Rule strip=Strip(Or(modifiers, Empty));
	Rule first=First(strip, type+"", beforeContent1);
	Rule aClass=Split(first, new_???(new_???(new_???())), children);
	return Tag(type, Strip(Suffix(aClass, "")));
}
Rule NameWithTypeParameters_Lang() {
	Rule name=CommonRules.StrippedIdentifier("");
	Rule withTypeParameters=Suffix(First(name, "", Expressions("", CommonRules.Identifier())), "");
	return Strip(Or(withTypeParameters, name));
}
Rule JStructureSegment_Lang() {
	LazyRule structureMember=new_???();
	structureMember.set(Or(Structures(structureMember), Statement(), JMethod(), LineComment(), BlockComment(), Whitespace()));
	return structureMember;
}
Rule BlockComment_Lang() {
	return Tag("", Strip(Prefix("", Suffix(String(""), ""))));
}
Rule LineComment_Lang() {
	return Tag("", Strip(Prefix("", String(""))));
}
Rule Statement_Lang() {
	Rule initialization=Initialization(JRules.JDefinition(), JExpression(JMethodSegment()));
	return Strip(Suffix(Or(initialization, JRules.JDefinition()), ""));
}
Rule JMethod_Lang() {
	Rule params=Parameters();
	Rule header=Strip(Suffix(Last(Node("", JRules.JDefinition()), "", params), ""));
	Rule withBody=Suffix(First(header, "", Statements("", JMethodSegment())), "");
	return Tag("", Strip(Or(Suffix(header, ""), withBody)));
}
Rule JMethodSegment_Lang() {
	LazyRule methodSegment=new_???();
	Rule expression=JExpression(methodSegment);
	Rule inner=JRules.JDefinition();
	methodSegment.set(Strip(Or(LineComment(), Switch("", expression, methodSegment), Conditional("", expression, methodSegment), Conditional("", expression, methodSegment), Else(methodSegment), Try(methodSegment), QuantityBlock("", "", inner, methodSegment), Block(methodSegment), BlockComment(), Strip(Suffix(JMethodStatementValue(methodSegment), "")), Whitespace())));
	return methodSegment;
}
Rule Try_Lang(LazyRule methodSegment) {
	Rule child=Node("", methodSegment);
	Rule definition=JRules.JDefinition();
	Rule value=JExpression(methodSegment);
	Rule definition1=Node("", definition);
	Rule value1=Node("", value);
	Rule resource=Node("", First(Or(Tag("", definition1)), "", value1));
	Splitter splitter=new_???(new_???(new_???(new_???())));
	Rule withResource=new_???("", Strip(Prefix("", new_???(resource, child, splitter, new_???()))));
	ContextRule withoutResource=new_???("", child);
	return Tag("", Prefix("", Or(withResource, withoutResource)));
}
Rule Block_Lang(LazyRule rule) {
	return Tag("", Strip(Prefix("", Suffix(Statements("", rule), ""))));
}
Rule JMethodStatementValue_Lang(Rule statement) {
	Rule expression=JExpression(statement);
	return Or(Break(), PostFix(expression), Return(expression), Yield(expression), Initialization(JRules.JDefinition(), expression), JRules.JDefinition(), Invokable(expression));
}
Rule Break_Lang() {
	return Tag("", Strip(Prefix("", Empty)));
}
Rule PostFix_Lang(Rule expression) {
	return Tag("", Strip(Suffix(Node("", expression), "")));
}
Rule Initialization_Lang(Rule definition, Rule value) {
	Rule definition1=Node("", definition);
	Rule value1=Node("", value);
	return First(Or(Tag("", definition1), Tag("", Node("", value))), "", value1);
}
Rule Invokable_Lang(Rule expression) {
	return Or(Invocation(expression), Invokable("", Strip(Prefix("", Node("", CType()))), expression));
}
Rule Invokable_Lang(String type, Rule caller, Rule expression) {
	Rule arguments=Expressions("", expression);
	FoldingDivider divider=new_???(new_???(new_???('(', ')')));
	Rule suffix=Strip(Suffix(Or(arguments, Whitespace()), String.valueOf(')')));
	return Tag(type, Split(Suffix(caller, String.valueOf('(')), KeepLast(divider), suffix));
}
Rule Yield_Lang(Rule expression) {
	return Tag("", Prefix("", Node("", expression)));
}
Rule Return_Lang(Rule expression) {
	return Tag("", Prefix("", Node("", expression)));
}
Rule Else_Lang(Rule statement) {
	return Tag("", Prefix("", Node("", statement)));
}
Rule Conditional_Lang(String tag, Rule inner, Rule statement) {
	return QuantityBlock(tag, "", inner, statement);
}
Rule QuantityBlock_Lang(String tag, String key, Rule inner, Rule statement) {
	Rule condition=Node(key, inner);
	Rule body=Node("", statement);
	Rule split=Split(Prefix("", condition), KeepFirst(new_???(new_???(new_???()))), body);
	return Tag(tag, Prefix(tag+"", Strip(split)));
}
Rule JExpression_Lang(Rule statement) {
	LazyRule expression=new_???();
	expression.set(Or(JLambda(statement, expression), Char(), Tag("", Strip(Prefix("", First(Node("", JRules.JType()), "", Node("", expression))))), Tag("", Strip(Prefix("", Suffix(Node("", expression), "")))), Tag("", Strip(Prefix("", Node("", expression)))), StringExpr(), Switch("", expression, CaseExprValue(statement, expression)), Index(expression), NewArray(expression), Index(expression), Invokable(expression), FieldAccess(expression), MethodAccess(expression), InstanceOf(expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), CommonRules.Identifier(), Number()));
	return expression;
}
Rule Number_Lang() {
	return Tag("", Strip(FilterRule.Number(String(""))));
}
Rule NewArray_Lang(LazyRule expression) {
	Rule type=Node("", JRules.JType());
	Rule tag=Tag("", Suffix(Expressions("", expression), ""));
	Rule tag1=Tag("", Node("", expression));
	Rule withoutArguments=Suffix(First(type, "", Node("", tag1)), "");
	Rule withArguments=Strip(First(type, "", Node("", tag)));
	return Tag("", Strip(Prefix("", Or(withoutArguments, withArguments))));
}
Rule MethodAccess_Lang(LazyRule expression) {
	Rule exprSource=Tag("", Node("", expression));
	Rule child=Tag("", Node("", JRules.JType()));
	return Tag("", Last(Node("", Or(exprSource, child)), "", CommonRules.StrippedIdentifier("")));
}
Rule CaseExprValue_Lang(Rule statement, LazyRule expression) {
	return Or(Tag("", Node("", Strip(Suffix(expression, "")))), Tag("", Node("", statement)));
}
Rule Char_Lang() {
	return Tag("", Strip(Prefix("", Suffix(String(""), ""))));
}
Rule JLambda_Lang(Rule statement, Rule expression) {
	Rule param=Tag("", CommonRules.StrippedIdentifier(""));
	Rule expressions=Tag("", Expressions("", CommonRules.StrippedIdentifier("")));
	Rule tag=Tag("", Empty);
	Rule strip=Or(Strip(Prefix("", Suffix(Or(expressions, tag), ""))), param);
	Rule child=Node("", Or(Tag("", Node("", statement)), Tag("", Node("", expression))));
	return Tag("", First(Node("", strip), "", child));
}
Rule InstanceOf_Lang(LazyRule expression) {
	Rule strip=Destruct();
	Rule type=Node("", Or(JRules.JDefinition(), JRules.JType(), strip));
	return Tag("", Last(Node("", expression), "", type));
}
Rule Destruct_Lang() {
	return Tag("", Strip(Suffix(First(Node("", JRules.JType()), "", Parameters()), "")));
}
Rule Index_Lang(LazyRule expression) {
	return Tag("", Strip(Suffix(Last(new_???("", expression), "", new_???("", expression)), "")));
}
Rule FieldAccess_Lang(Rule expression) {
	Rule child=Node("", expression);
	Rule rightRule=CommonRules.StrippedIdentifier("");
	Splitter splitter=new_???("", new_???());
	return Tag("", new_???(child, rightRule, splitter, new_???()));
}
Rule StringExpr_Lang() {
	return Tag("", Strip(Prefix("", Suffix(Or(String(""), Empty), ""))));
}
Rule Operator_Lang(String type, String infix, LazyRule expression) {
	Rule left=Node("", expression);
	Rule right=Node("", expression);
	Splitter splitter=DividingSplitter.KeepFirst(new_???(new_???(new_???(infix))));
	return Tag(type, SplitRule.Split(left, splitter, right));
}
Rule Switch_Lang(String group, Rule expression, Rule rule) {
	Rule cases=Statements("", Strip(Or(Case(group, rule, expression), Empty)));
	Rule value=Prefix("", Suffix(Node("", expression), ""));
	return Tag(""+group, Strip(Prefix("", Suffix(First(Strip(value), "", cases), ""))));
}
Rule Case_Lang(String group, Rule rule, Rule expression) {
	Rule target=Node("", Or(JRules.JDefinition(), Destruct()));
	Rule defaultCase=Strip(Prefix("", Empty));
	Rule withWhen=Last(target, "", Node("", expression));
	Rule value=First(Or(defaultCase, Prefix("", Or(withWhen, target))), "", Node("", rule));
	return Tag(""+group, value);
}
Rule Invocation_Lang(Rule expression) {
	return Invokable("", Node("", expression), expression);
}
Rule Invalid_Lang() {
	return Tag("", Placeholder(String("")));
}
Rule Parameters_Lang() {
	return Expressions("", Or(JRules.JDefinition(), Whitespace()));
}
