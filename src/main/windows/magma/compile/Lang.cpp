// Generated transpiled C++ from 'src\main\java\magma\compile\Lang.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Lang {};
struct JavaRootSegment {};
struct CRootSegment {};
Option<> after_CRootSegment() {
}
struct JStructureSegment {};
struct JExpression {};
struct JMethodSegment {};
struct CFunctionSegment {};
struct JType {};
struct CType {};
char* stringify_CType() {
}
struct JStructure {};
char* name_JStructure() {
}
Option<> typeParameters_JStructure() {
}
List<> children_JStructure() {
}
struct CParameter {};
struct CExpression {};
struct InstanceOfTarget {};
struct CaseTarget {};
struct CaseExprValue {};
struct LambdaValue {};
struct CharNode {char* value;};
struct CAnd {CExpression left;CExpression right;};
struct And {JExpression left;JExpression right;};
struct Destruct {JType type;List<> params;};
struct InstanceOf {JExpression child;InstanceOfTarget target;};
struct Wildcard {};
struct JAdd {JExpression left;JExpression right;};
struct JSubtract {JExpression left;JExpression right;};
struct JEquals {JExpression left;JExpression right;};
struct CEquals {CExpression left;CExpression right;};
struct JString {Option<> content;};
struct CAdd {CExpression left;CExpression right;};
struct CString {char* content;};
struct JFieldAccess {JExpression child;char* name;};
struct CFieldAccess {CExpression child;char* name;};
struct JConstruction {JType type;Option<> arguments;};
struct JInvocation {JExpression caller;Option<> arguments;};
struct Not {JExpression child;};
struct ExprCaseExprValue {JExpression expression;};
struct StatementCaseExprValue {JMethodSegment statement;};
record CaseExpr_Lang(Option<> target, CaseExprValue value) {
}
record CaseStatement_Lang(Option<> target, JMethodSegment value) {
}
struct SwitchExpr {JExpression value;List<> cases;};
struct SwitchStatement {JExpression value;List<> cases;};
struct ExprLambdaValue {JExpression child;};
struct StatementLambdaValue {JMethodSegment child;};
struct Lambda {Option<> param;LambdaValue child;};
struct NewArray {JType type;JExpression length;};
struct CAssignment {CExpression location;CExpression value;};
struct CPostFix {CExpression value;};
struct JAssignment {JExpression location;JExpression value;};
struct JPostFix {JExpression value;};
struct JInitialization {JDefinition definition;JExpression value;};
struct CInitialization {CDefinition definition;CExpression value;};
struct CBlock {List<> children;};
struct JBlock {List<> children;};
struct JIf {JExpression condition;JMethodSegment body;};
struct CIf {CExpression condition;CFunctionSegment body;};
struct JWhile {JExpression condition;JMethodSegment body;};
struct CWhile {CExpression condition;CFunctionSegment body;};
struct Field {JDefinition value;};
struct JGeneric {char* base;Option<> typeArguments;};
struct CGeneric {char* base;List<> typeArguments;};
char* stringify_CGeneric() {
	return base+""+typeArguments.stream().map(CType::stringify).collect(Collectors.joining("_"));
}
struct Array {JType child;};
struct JDefinition {char* name;JType type;Option<> modifiers;Option<> typeParameters;};
record Modifier_Lang(char* value) {
}
struct Method {JDefinition definition;Option<> params;Option<> body;Option<> typeParameters;};
struct Invalid {char* value;Option<> after;};
public Invalid_Invalid(char* value) {
	this((value, new_???(());
}
char* stringify_Invalid() {
	return "???";
}
struct JClass {Option<> modifiers;char* name;List<> children;Option<> typeParameters;Option<> interfaces;};
struct Interface {Option<> modifiers;char* name;List<> children;Option<> typeParameters;Option<> interfaces;Option<> superclasses;Option<> variants;};
struct Record {Option<> modifiers;char* name;List<> children;Option<> typeParameters;Option<> params;Option<> interfaces;};
struct Structure {char* name;List<> fields;Option<> after;Option<> typeParameters;};
struct Whitespace {};
struct Placeholder {char* value;};
record JavaRoot_Lang(List<> children) {
}
record CRoot_Lang(List<> children) {
}
struct Import {char* location;};
struct Package {char* location;};
struct CDefinition {char* name;CType type;Option<> typeParameters;};
struct CFunctionPointerDefinition {char* name;CType returnType;List<> paramTypes;};
struct Function {CDefinition definition;List<> params;List<> body;Option<> after;Option<> typeParameters;};
struct Identifier {char* value;};
char* stringify_Identifier() {
	return value;
}
struct Pointer {CType child;};
char* stringify_Pointer() {
	return child.stringify()+"";
}
struct FunctionPointer {CType returnType;List<> paramTypes;};
char* stringify_FunctionPointer() {
	return ""+paramTypes.stream().map(CType::stringify).collect(Collectors.joining("_"))+""+returnType.stringify();
}
struct LineComment {char* value;};
struct BlockComment {char* value;};
struct JReturn {JExpression value;};
struct CReturn {CExpression value;};
struct JElse {JMethodSegment child;};
struct CElse {CFunctionSegment child;};
struct CInvocation {CExpression caller;List<> arguments;};
struct Break {};
struct InvocationFolder {char open;char close;};
DivideState fold_InvocationFolder(DivideState state, char c) {
	DivideState appended=state.append(c);
	if (c==open)
	{
	DivideState enter=appended.enter();
	return enter.advance();
	return enter;}
	return appended.exit();
	return appended;
}
char* delimiter_InvocationFolder() {
	return String.valueOf(open);
}
struct Index {JExpression child;JExpression index;};
struct Quantity {JExpression child;};
struct Cast {JType type;JExpression child;};
struct JLessThanEquals {JExpression left;JExpression right;};
struct JGreaterThanEquals {JExpression left;JExpression right;};
struct JLessThan {JExpression left;JExpression right;};
struct Try {JMethodSegment child;};
struct Catch {JDefinition definition;JMethodSegment body;};
struct Yield {JExpression child;};
struct Variadic {JType child;};
struct MyFolder {};
DivideState fold_MyFolder(DivideState state, char c) {
	if (c=='(')return state.append(c).enter();
	if (c==')')return state.advance();
	return state.exit().append(c);
	return state.append(c);
}
char* delimiter_MyFolder() {
	return "";
}
Rule CRoot_Lang() {
	return Statements(("", Strip(("", Or((CStructure((), Function(()), ""));
}
Rule Function_Lang() {
	NodeRule definition=new_???(("", CDefinition(());
	Rule params=Expressions(("", Or((CFunctionPointerDefinition((), CDefinition(()));
	Rule body=Statements(("", CFunctionSegment(());
	Rule first=First((definition, "", params);
	Rule suffix=Suffix((first, "");
	Rule suffix1=Suffix((body, System.lineSeparator()+"");
	Rule functionDecl=First((suffix, "", suffix1);
	Rule templateParams=Expressions(("", Prefix(("", Identifier(()));
	Rule templateDecl=NonEmptyList(("", Prefix(("", Suffix((templateParams, ""+System.lineSeparator())));
	Rule maybeTemplate=Or((templateDecl, Empty);
	return Tag(("", First((maybeTemplate, "", functionDecl));
}
Rule CFunctionPointerDefinition_Lang() {
	return Tag(("", Suffix((First((Suffix((First((Node(("", CType(()), "", String(("")), ""), "", Expressions(("", CType(())), ""));
}
Rule CDefinition_Lang() {
	return Last((Node(("", CType(()), "", new_???((""));
}
Rule CType_Lang() {
	new LazyRule();
	Rule funcPtr=Tag(("", Suffix((First((Node(("", rule), "", Expressions(("", rule)), ""));
	rule.set((Or((funcPtr, Identifier((), Tag(("", Suffix((Node(("", rule), "")), Generic((rule), Invalid(()));
	return rule;
}
Rule CStructure_Lang() {
	Rule plainName=StrippedIdentifier(("");
	Rule structPrefix=Prefix(("", plainName);
	Rule fields=Statements(("", Suffix((CDefinition((), ""));
	Rule structWithFields=Suffix((First((structPrefix, "", fields), "");
	Rule structComplete=Suffix((structWithFields, "");
	Rule templateParams=Expressions(("", Prefix(("", Identifier(()));
	Rule templateDecl=NonEmptyList(("", Prefix(("", Suffix((templateParams, ""+System.lineSeparator())));
	Rule maybeTemplate=Or((templateDecl, Empty);
	return Tag(("", First((maybeTemplate, "", structComplete));
}
Rule JRoot_Lang() {
	Rule segment=Or((Namespace((""), Namespace((""), Structures((JStructureSegment(()), BlockComment((), Whitespace(());
	return Statements(("", segment);
}
Rule Structures_Lang(Rule structureMember) {
	return Or((JStructure(("", structureMember), JStructure(("", structureMember), JStructure(("", structureMember));
}
Rule Whitespace_Lang() {
	return Tag(("", Strip((Empty));
}
Rule Namespace_Lang(char* type) {
	return Tag((type, Strip((Prefix((type+"", Suffix((String((""), ""))));
}
Rule JStructure_Lang(char* type, Rule rule) {
	Rule modifiers=String(("");
	Rule maybeWithTypeArguments=NameWithTypeParameters(();
	Rule maybeWithParameters=Strip((Or((Suffix((First((maybeWithTypeArguments, "", Parameters(()), ""), maybeWithTypeArguments));
	Rule maybeWithParameters1=Or((Last((maybeWithParameters, "", Expressions(("", JType(())), maybeWithParameters);
	Rule beforeContent=Or((Last((maybeWithParameters1, "", Expressions(("", JType(())), maybeWithParameters1);
	Rule children=Statements(("", rule);
	Rule beforeContent1=Or((Last((beforeContent, "", Delimited(("", JType((), "")), beforeContent);
	Rule strip=Strip((Or((modifiers, Empty));
	Rule first=First((strip, type+"", beforeContent1);
	Rule aClass=Split((first, new_???((new_???((new_???(())), children);
	return Tag((type, Strip((Suffix((aClass, "")));
}
Rule NameWithTypeParameters_Lang() {
	Rule name=StrippedIdentifier(("");
	Rule withTypeParameters=Suffix((First((name, "", Expressions(("", Identifier(())), "");
	return Strip((Or((withTypeParameters, name));
}
Rule JStructureSegment_Lang() {
	new LazyRule();
	structureMember.set((Or((Structures((structureMember), Statement((), JMethod((), LineComment((), BlockComment((), Whitespace(()));
	return structureMember;
}
Rule BlockComment_Lang() {
	return Tag(("", Strip((Prefix(("", Suffix((String((""), ""))));
}
Rule LineComment_Lang() {
	return Tag(("", Strip((Prefix(("", String((""))));
}
Rule Statement_Lang() {
	Rule initialization=Initialization((JDefinition((), JExpression((JMethodSegment(()));
	return Strip((Suffix((Or((initialization, JDefinition(()), ""));
}
Rule JMethod_Lang() {
	Rule params=Parameters(();
	Rule header=Strip((Suffix((Last((Node(("", JDefinition(()), "", params), ""));
	Rule withBody=Suffix((First((header, "", Statements(("", JMethodSegment(())), "");
	return Tag(("", Strip((Or((Suffix((header, ""), withBody)));
}
Rule JMethodSegment_Lang() {
	new LazyRule();
	Rule expression=JExpression((methodSegment);
	Rule inner=JDefinition(();
	methodSegment.set((Strip((Or((Whitespace((), LineComment((), Switch(("", expression, methodSegment), Conditional(("", expression, methodSegment), Conditional(("", expression, methodSegment), Else((methodSegment), Try((methodSegment), QuantityBlock(("", "", inner, methodSegment), Strip((Suffix((JMethodStatementValue((methodSegment), "")), Block((methodSegment), BlockComment(())));
	return methodSegment;
}
Rule Try_Lang(LazyRule methodSegment) {
	Rule child=Node(("", methodSegment);
	Rule resource=Node(("", Initialization((JDefinition((), JExpression((methodSegment)));
	Splitter splitter=new_???((new_???((new_???((new_???(())));
	Rule withResource=new_???(("", Strip((Prefix(("", new_???((resource, child, splitter))));
	ContextRule withoutResource=new_???(("", child);
	return Tag(("", Prefix(("", Or((withResource, withoutResource)));
}
Rule Block_Lang(LazyRule rule) {
	return Tag(("", Strip((Prefix(("", Suffix((Statements(("", rule), ""))));
}
Rule JMethodStatementValue_Lang(Rule statement) {
	Rule expression=JExpression((statement);
	return Or((Break((), Return((expression), Yield((expression), Invokable((expression), Initialization((JDefinition((), expression), PostFix((expression), JDefinition(());
}
Rule Break_Lang() {
	return Tag(("", Strip((Prefix(("", Empty)));
}
Rule PostFix_Lang(Rule expression) {
	return Tag(("", Strip((Suffix((Node(("", expression), "")));
}
Rule Initialization_Lang(Rule definition, Rule value) {
	Rule definition1=Node(("", definition);
	Rule value1=Node(("", value);
	return First((Or((Tag(("", definition1), Tag(("", Node(("", value))), "", value1);
}
Rule Invokable_Lang(Rule expression) {
	return Or((Invocation((expression), Invokable(("", Strip((Prefix(("", Node(("", CType(()))), expression));
}
Rule Invokable_Lang(char* type, Rule caller, Rule expression) {
	Rule arguments=Expressions(("", expression);
	FoldingDivider divider=new_???((new_???((new_???(('(', ')')));
	Rule suffix=Strip((Suffix((Or((arguments, Whitespace(()), String.valueOf(')')));
	return Tag((type, Split((Suffix((caller, String.valueOf('(')), KeepLast((divider), suffix));
}
Rule Yield_Lang(Rule expression) {
	return Tag(("", Prefix(("", Node(("", expression)));
}
Rule Return_Lang(Rule expression) {
	return Tag(("", Prefix(("", Node(("", expression)));
}
Rule Else_Lang(Rule statement) {
	return Tag(("", Prefix(("", Node(("", statement)));
}
Rule Conditional_Lang(char* tag, Rule inner, Rule statement) {
	return QuantityBlock((tag, "", inner, statement);
}
Rule QuantityBlock_Lang(char* tag, char* key, Rule inner, Rule statement) {
	Rule condition=Node((key, inner);
	Rule body=Node(("", statement);
	Rule split=Split((Prefix(("", condition), KeepFirst((new_???((new_???((new_???(()))), body);
	return Tag((tag, Prefix((tag+"", Strip((split)));
}
Rule JExpression_Lang(Rule statement) {
	new LazyRule();
	expression.set((Or((Lambda((statement, expression), Char((), Tag(("", Strip((Prefix(("", First((Node(("", JType(()), "", Node(("", expression))))), Tag(("", Strip((Prefix(("", Suffix((Node(("", expression), "")))), Tag(("", Strip((Prefix(("", Node(("", expression)))), StringExpr((), Switch(("", expression, CaseExprValue((statement, expression)), Index((expression), Tag(("", Strip((Suffix((First((Prefix(("", Node(("", JType(())), "", Node(("", expression)), ""))), Index((expression), Invokable((expression), FieldAccess((expression), Tag(("", Last((Node(("", expression), "", StrippedIdentifier((""))), InstanceOf((expression), Operator(("", "", expression), Operator(("", "", expression), Operator(("", "", expression), Operator(("", "", expression), Operator(("", "", expression), Operator(("", "", expression), Operator(("", "", expression), Identifier(()));
	return expression;
}
Rule CaseExprValue_Lang(Rule statement, LazyRule expression) {
	return Or((Tag(("", Node(("", Strip((Suffix((expression, "")))), Tag(("", Node(("", statement)));
}
Rule Char_Lang() {
	return Tag(("", Strip((Prefix(("", Suffix((String((""), ""))));
}
Rule Lambda_Lang(Rule statement, Rule expression) {
	Rule strip=Or((Strip((Prefix(("", Empty)), StrippedIdentifier((""));
	Rule child=Node(("", Or((Tag(("", Node(("", statement)), Tag(("", Node(("", expression))));
	return Tag(("", First((strip, "", child));
}
Rule InstanceOf_Lang(LazyRule expression) {
	Rule strip=Destruct(();
	Rule type=Node(("", Or((JDefinition((), JType((), strip));
	return Tag(("", Last((Node(("", expression), "", type));
}
Rule Destruct_Lang() {
	return Tag(("", Strip((Suffix((First((Node(("", JType(()), "", Parameters(()), "")));
}
Rule Index_Lang(LazyRule expression) {
	return Tag(("", Strip((Suffix((Last((new_???(("", expression), "", new_???(("", expression)), "")));
}
Rule FieldAccess_Lang(Rule expression) {
	return Tag(("", Last((Node(("", expression), "", Strip((String((""))));
}
Rule StringExpr_Lang() {
	return Tag(("", Strip((Prefix(("", Suffix((Or((String((""), Empty), ""))));
}
Rule Operator_Lang(char* type, char* infix, LazyRule expression) {
	return Tag((type, First((Node(("", expression), infix, Node(("", expression)));
}
Rule Switch_Lang(char* group, Rule expression, Rule rule) {
	Rule cases=Statements(("", Strip((Or((Case((group, rule), Empty)));
	Rule value=Prefix(("", Suffix((Node(("", expression), ""));
	return Tag((""+group, Strip((Prefix(("", Suffix((First((Strip((value), "", cases), ""))));
}
Rule Case_Lang(char* group, Rule rule) {
	Rule after=Node(("", Or((JDefinition((), Destruct(()));
	Rule defaultCase=Strip((Prefix(("", Empty));
	Rule value=First((Or((defaultCase, Prefix(("", after)), "", Node(("", rule));
	return Tag((""+group, value);
}
Rule CExpression_Lang() {
	new LazyRule();
	expression.set((Or((Invocation((expression), FieldAccess((expression), Operator(("", "", expression), Operator(("", "", expression), Operator(("", "", expression), StringExpr((), Identifier((), Char((), Invalid(()));
	return expression;
}
Rule Invocation_Lang(Rule expression) {
	return Invokable(("", Node(("", expression), expression);
}
Rule CFunctionSegment_Lang() {
	new LazyRule();
	rule.set((Or((Whitespace((), Prefix((System.lineSeparator()+"", CFunctionSegmentValue((rule)), Invalid(()));
	return rule;
}
Rule Invalid_Lang() {
	return Tag(("", Placeholder((String(("")));
}
Rule CFunctionSegmentValue_Lang(LazyRule rule) {
	return Or((LineComment((), Conditional(("", CExpression((), rule), Conditional(("", CExpression((), rule), Break((), Else((rule), CFunctionStatement((), Block((rule));
}
Rule CFunctionStatement_Lang() {
	new LazyRule();
	functionStatement.set((Or((Conditional(("", CExpression((), functionStatement), Suffix((CFunctionStatementValue((), "")));
	return functionStatement;
}
Rule CFunctionStatementValue_Lang() {
	Rule expression=CExpression(();
	return Or((Return((expression), Invocation((expression), Initialization((CDefinition((), expression), CDefinition((), PostFix((expression));
}
Rule Parameters_Lang() {
	return Expressions(("", Or((JDefinition((), Whitespace(()));
}
Rule JDefinition_Lang() {
	Rule type=Node(("", JType(());
	Rule name=String(("");
	Rule modifiers=Delimited(("", Tag(("", String(("")), "");
	Rule withModifiers=Split((modifiers, KeepLast((new_???((new_???(())), type);
	Rule beforeName=Or((withModifiers, type);
	return Tag(("", Strip((Last((beforeName, "", name)));
}
Rule JType_Lang() {
	new LazyRule();
	type.set((Or((Generic((type), Array((type), Identifier((), WildCard((), Tag(("", Strip((Suffix((Node(("", type), "")))));
	return type;
}
Rule WildCard_Lang() {
	return Tag(("", Strip((Prefix(("", Empty)));
}
Rule Array_Lang(Rule type) {
	return Tag(("", Strip((Suffix((Node(("", type), "")));
}
Rule Identifier_Lang() {
	return Tag(("", StrippedIdentifier((""));
}
Rule StrippedIdentifier_Lang(char* key) {
	return Strip(FilterRule.Identifier(String(key)));
}
Rule Generic_Lang(Rule type) {
	Rule base=Strip((String((""));
	Rule arguments=Or((Expressions(("", type), Strip((Empty));
	return Tag(("", Strip((Suffix((First((base, "", arguments), "")));
}
