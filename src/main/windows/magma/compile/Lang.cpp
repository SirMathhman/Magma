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
struct JStructure {};
/*???*/ name_JStructure() {
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
struct LambdaParamSet {};
struct MethodAccessSource {};
struct NewArrayValue {};
struct Identifiable {};
struct CharNode {/*???*/ value;};
struct CAnd {/*???*/ left;/*???*/ right;};
struct And {/*???*/ left;/*???*/ right;};
struct Destruct {/*???*/ type;List<> params;};
struct InstanceOf {/*???*/ child;/*???*/ target;};
struct Wildcard {};
struct JAdd {/*???*/ left;/*???*/ right;};
struct JSubtract {/*???*/ left;/*???*/ right;};
struct JEquals {/*???*/ left;/*???*/ right;};
struct JNotEquals {/*???*/ left;/*???*/ right;};
struct CEquals {/*???*/ left;/*???*/ right;};
struct JString {Option<> content;};
struct CAdd {/*???*/ left;/*???*/ right;};
struct CString {/*???*/ content;};
struct JFieldAccess {/*???*/ child;/*???*/ name;};
struct CFieldAccess {/*???*/ child;/*???*/ name;};
struct JConstruction {/*???*/ type;Option<> arguments;};
struct JInvocation {/*???*/ caller;Option<> arguments;};
struct Not {/*???*/ child;};
struct ExprCaseExprValue {/*???*/ expression;};
struct StatementCaseExprValue {/*???*/ statement;};
/*???*/ CaseExpr_Lang(Option<> target, /*???*/ value, Option<> when) {
}
/*???*/ CaseStatement_Lang(Option<> target, /*???*/ value, Option<> when) {
}
struct SwitchExpr {/*???*/ value;List<> cases;};
struct SwitchStatement {/*???*/ value;List<> cases;};
struct ExprLambdaValue {/*???*/ child;};
struct StatementLambdaValue {/*???*/ child;};
struct Lambda {/*???*/ params;/*???*/ child;};
struct LengthNewArrayValue {/*???*/ length;};
struct ArgumentsNewArrayValue {Option<> arguments;};
struct NewArray {/*???*/ type;/*???*/ value;};
struct CAssignment {/*???*/ location;/*???*/ value;};
struct CPostFix {/*???*/ value;};
struct JAssignment {/*???*/ location;/*???*/ value;};
struct JPostFix {/*???*/ value;};
struct JInitialization {/*???*/ definition;/*???*/ value;};
struct CInitialization {/*???*/ definition;/*???*/ value;};
struct CBlock {List<> children;};
struct JBlock {List<> children;};
struct JIf {/*???*/ condition;/*???*/ body;};
struct CIf {/*???*/ condition;/*???*/ body;};
struct JWhile {/*???*/ condition;/*???*/ body;};
struct CWhile {/*???*/ condition;/*???*/ body;};
struct Field {/*???*/ value;};
struct JGeneric {/*???*/ base;Option<> typeArguments;};
struct CTemplate {/*???*/ base;List<> typeArguments;};
/*???*/ stringify_CTemplate() {
	return base+""+typeArguments.stream().map(/*???*/).collect(new_???(""));
}
struct Array {/*???*/ child;};
struct JDefinition {/*???*/ name;/*???*/ type;Option<> modifiers;Option<> typeParameters;};
/*???*/ Modifier_Lang(/*???*/ value) {
}
struct Method {/*???*/ definition;Option<> params;Option<> body;Option<> typeParameters;};
struct Invalid {/*???*/ value;Option<> after;};
/*???*/ Invalid_Invalid(/*???*/ value) {
	this(value, new_???());
}
/*???*/ stringify_Invalid() {
	return "";
}
struct JClass {Option<> modifiers;/*???*/ name;List<> children;Option<> typeParameters;Option<> interfaces;};
struct Interface {Option<> modifiers;/*???*/ name;List<> children;Option<> typeParameters;Option<> interfaces;Option<> superclasses;Option<> variants;};
struct RecordNode {Option<> modifiers;/*???*/ name;List<> children;Option<> typeParameters;Option<> params;Option<> interfaces;};
struct Structure {/*???*/ name;List<> fields;Option<> after;Option<> typeParameters;};
struct Whitespace {};
struct Placeholder {/*???*/ value;};
/*???*/ JRoot_Lang(List<> children) {
}
/*???*/ CRoot_Lang(List<> children) {
}
struct Import {/*???*/ location;};
struct Package {/*???*/ location;};
struct CDefinition {/*???*/ name;/*???*/ type;Option<> typeParameters;};
struct CFunctionPointerDefinition {/*???*/ name;/*???*/ returnType;List<> paramTypes;};
struct Function {/*???*/ definition;List<> params;List<> body;Option<> after;Option<> typeParameters;};
struct Identifier {/*???*/ value;};
/*???*/ stringify_Identifier() {
	/*???*/ value;
}
struct Pointer {/*???*/ child;};
/*???*/ stringify_Pointer() {
	return child.stringify()+"";
}
struct LineComment {/*???*/ value;};
struct BlockComment {/*???*/ value;};
struct JReturn {/*???*/ value;};
struct CReturn {/*???*/ value;};
struct JElse {/*???*/ child;};
struct CElse {/*???*/ child;};
struct CInvocation {/*???*/ caller;List<> arguments;};
struct Break {};
struct InvocationFolder {/*???*/ open;/*???*/ close;};
/*???*/ fold_InvocationFolder(/*???*/ state, /*???*/ c) {
	/*???*/ appended=state.append(c);
	if (c==open)
	{
	/*???*/ enter=appended.enter();
	if (enter.isShallow())return enter.advance();
	else
	/*???*/ enter;}
	if (c==close)return appended.exit();
	/*???*/ appended;
}
/*???*/ delimiter_InvocationFolder() {
	return "";
}
struct Index {/*???*/ child;/*???*/ index;};
struct Quantity {/*???*/ child;};
struct Cast {/*???*/ type;/*???*/ child;};
struct JLessThanEquals {/*???*/ left;/*???*/ right;};
struct JGreaterThan {/*???*/ left;/*???*/ right;};
struct JOr {/*???*/ left;/*???*/ right;};
struct JGreaterThanEquals {/*???*/ left;/*???*/ right;};
struct JLessThan {/*???*/ left;/*???*/ right;};
struct Try {/*???*/ child;Option<> resource;};
struct Catch {/*???*/ definition;/*???*/ body;};
struct Yield {/*???*/ child;};
struct Variadic {/*???*/ child;};
struct MyFolder {};
/*???*/ fold_MyFolder(/*???*/ state, /*???*/ c) {
	if (c=='(')return state.append(c).enter();
	if (c==')')if (state.isLevel())return state.advance();
	else
	return state.exit().append(c);
	return state.append(c);
}
/*???*/ delimiter_MyFolder() {
	return "";
}
struct EmptyLambdaParam {};
struct SingleLambdaParam {/*???*/ param;};
struct MultipleLambdaParam {Option<> params;};
struct ExprMethodAccessSource {/*???*/ child;};
struct TypeMethodAccessSource {/*???*/ child;};
struct MethodAccess {/*???*/ name;/*???*/ source;};
struct NumberNode {/*???*/ number;};
struct OperatorFolder {/*???*/ operator;};
/*???*/ fold_OperatorFolder(/*???*/ state, /*???*/ c) {
	if (c==operator.charAt(/*???*/))
	{
	if (/*???*/)
	{
	state.pop();
	return state.advance();}
	return state.advance();}
	return state.append(c);
}
/*???*/ delimiter_OperatorFolder() {
	/*???*/ operator;
}
/*???*/ QualifiedSegment_Lang(/*???*/ value) {
}
struct JQualified {Option<> segments;};
/*???*/ last_JQualified() {
	if (/*???*/)return "";
	return segments.orElse(new_???()).getLast().map(/*???*/.value).orElse("");
}
/*???*/ CFunctionPointerDefinition_Lang() {
	return Tag("", Suffix(First(Suffix(First(Node("", CType()), "", String("")), ""), "", Expressions("", CType())), ""));
}
/*???*/ CDefinition_Lang() {
	return Last(Node("", CType()), "", new_???(""));
}
/*???*/ CType_Lang() {
	/*???*/ rule=new_???();
	/*???*/ funcPtr=Tag("", Suffix(First(Node("", rule), "", Expressions("", rule)), ""));
	rule.set(Or(funcPtr, CommonRules.Identifier(), Tag("", Suffix(Node("", rule), "")), JRules.Parameterized("", rule, String("")), Invalid()));
	/*???*/ rule;
}
/*???*/ CStructure_Lang() {
	/*???*/ plainName=CommonRules.StrippedIdentifier("");
	/*???*/ structPrefix=Prefix("", plainName);
	/*???*/ fields=Statements("", Suffix(CDefinition(), ""));
	/*???*/ structWithFields=Suffix(First(structPrefix, "", fields), "");
	/*???*/ structComplete=Suffix(structWithFields, "");
	/*???*/ templateParams=Expressions("", Prefix("", CommonRules.Identifier()));
	/*???*/ templateDecl=NonEmptyList("", Prefix("", Suffix(templateParams, ""+System.lineSeparator())));
	/*???*/ maybeTemplate=Or(templateDecl, Empty);
	return Tag("", First(maybeTemplate, "", structComplete));
}
/*???*/ JRoot_Lang() {
	/*???*/ segment=Or(Namespace(""), Namespace(""), Structures(JStructureSegment()), BlockComment(), Whitespace());
	return Statements("", segment);
}
/*???*/ Structures_Lang(/*???*/ structureMember) {
	return Or(JStructure("", structureMember), JStructure("", structureMember), JStructure("", structureMember));
}
/*???*/ Whitespace_Lang() {
	return Tag("", Strip(Empty));
}
/*???*/ Namespace_Lang(/*???*/ type) {
	return Tag(type, Strip(Prefix(type+"", Suffix(String(""), ""))));
}
/*???*/ JStructure_Lang(/*???*/ type, /*???*/ rule) {
	/*???*/ modifiers=String("");
	/*???*/ maybeWithTypeArguments=NameWithTypeParameters();
	/*???*/ maybeWithParameters=Strip(Or(Suffix(First(maybeWithTypeArguments, "", Parameters()), ""), maybeWithTypeArguments));
	/*???*/ maybeWithParameters1=Or(Last(maybeWithParameters, "", Expressions("", JRules.JType())), maybeWithParameters);
	/*???*/ beforeContent=Or(Last(maybeWithParameters1, "", Expressions("", JRules.JType())), maybeWithParameters1);
	/*???*/ children=Statements("", rule);
	/*???*/ beforeContent1=Or(Last(beforeContent, "", Delimited("", JRules.JType(), "")), beforeContent);
	/*???*/ strip=Strip(Or(modifiers, Empty));
	/*???*/ first=First(strip, type+"", beforeContent1);
	/*???*/ aClass=Split(first, new_???(new_???(new_???())), children);
	return Tag(type, Strip(Suffix(aClass, "")));
}
/*???*/ NameWithTypeParameters_Lang() {
	/*???*/ name=CommonRules.StrippedIdentifier("");
	/*???*/ withTypeParameters=Suffix(First(name, "", Expressions("", CommonRules.Identifier())), "");
	return Strip(Or(withTypeParameters, name));
}
/*???*/ JStructureSegment_Lang() {
	/*???*/ structureMember=new_???();
	structureMember.set(Or(Structures(structureMember), Statement(), JMethod(), LineComment(), BlockComment(), Whitespace()));
	/*???*/ structureMember;
}
/*???*/ BlockComment_Lang() {
	return Tag("", Strip(Prefix("", Suffix(String(""), ""))));
}
/*???*/ LineComment_Lang() {
	return Tag("", Strip(Prefix("", String(""))));
}
/*???*/ Statement_Lang() {
	/*???*/ initialization=Initialization(JRules.JDefinition(), JExpression(JMethodSegment()));
	return Strip(Suffix(Or(initialization, JRules.JDefinition()), ""));
}
/*???*/ JMethod_Lang() {
	/*???*/ params=Parameters();
	/*???*/ header=Strip(Suffix(Last(Node("", JRules.JDefinition()), "", params), ""));
	/*???*/ withBody=Suffix(First(header, "", Statements("", JMethodSegment())), "");
	return Tag("", Strip(Or(Suffix(header, ""), withBody)));
}
/*???*/ JMethodSegment_Lang() {
	/*???*/ methodSegment=new_???();
	/*???*/ expression=JExpression(methodSegment);
	/*???*/ inner=JRules.JDefinition();
	methodSegment.set(Strip(Or(LineComment(), Switch("", expression, methodSegment), Conditional("", expression, methodSegment), Conditional("", expression, methodSegment), Else(methodSegment), Try(methodSegment), QuantityBlock("", "", inner, methodSegment), Block(methodSegment), BlockComment(), Strip(Suffix(JMethodStatementValue(methodSegment), "")), Whitespace())));
	/*???*/ methodSegment;
}
/*???*/ Try_Lang(/*???*/ methodSegment) {
	/*???*/ child=Node("", methodSegment);
	/*???*/ definition=JRules.JDefinition();
	/*???*/ value=JExpression(methodSegment);
	/*???*/ definition1=Node("", definition);
	/*???*/ value1=Node("", value);
	/*???*/ resource=Node("", First(Or(Tag("", definition1)), "", value1));
	/*???*/ splitter=new_???(new_???(new_???(new_???())));
	/*???*/ withResource=new_???("", Strip(Prefix("", new_???(resource, child, splitter, new_???()))));
	/*???*/ withoutResource=new_???("", child);
	return Tag("", Prefix("", Or(withResource, withoutResource)));
}
/*???*/ Block_Lang(/*???*/ rule) {
	return Tag("", Strip(Prefix("", Suffix(Statements("", rule), ""))));
}
/*???*/ JMethodStatementValue_Lang(/*???*/ statement) {
	/*???*/ expression=JExpression(statement);
	return Or(Break(), PostFix(expression), Return(expression), Yield(expression), Initialization(JRules.JDefinition(), expression), JRules.JDefinition(), Invokable(expression));
}
/*???*/ Break_Lang() {
	return Tag("", Strip(Prefix("", Empty)));
}
/*???*/ PostFix_Lang(/*???*/ expression) {
	return Tag("", Strip(Suffix(Node("", expression), "")));
}
/*???*/ Initialization_Lang(/*???*/ definition, /*???*/ value) {
	/*???*/ definition1=Node("", definition);
	/*???*/ value1=Node("", value);
	return First(Or(Tag("", definition1), Tag("", Node("", value))), "", value1);
}
/*???*/ Invokable_Lang(/*???*/ expression) {
	return Or(Invocation(expression), Invokable("", Strip(Prefix("", Node("", CType()))), expression));
}
/*???*/ Invokable_Lang(/*???*/ type, /*???*/ caller, /*???*/ expression) {
	/*???*/ arguments=Expressions("", expression);
	/*???*/ divider=new_???(new_???(new_???('(', ')')));
	/*???*/ suffix=Strip(Suffix(Or(arguments, Whitespace()), String.valueOf(')')));
	return Tag(type, Split(Suffix(caller, String.valueOf('(')), KeepLast(divider), suffix));
}
/*???*/ Yield_Lang(/*???*/ expression) {
	return Tag("", Prefix("", Node("", expression)));
}
/*???*/ Return_Lang(/*???*/ expression) {
	return Tag("", Prefix("", Node("", expression)));
}
/*???*/ Else_Lang(/*???*/ statement) {
	return Tag("", Prefix("", Node("", statement)));
}
/*???*/ Conditional_Lang(/*???*/ tag, /*???*/ inner, /*???*/ statement) {
	return QuantityBlock(tag, "", inner, statement);
}
/*???*/ QuantityBlock_Lang(/*???*/ tag, /*???*/ key, /*???*/ inner, /*???*/ statement) {
	/*???*/ condition=Node(key, inner);
	/*???*/ body=Node("", statement);
	/*???*/ split=Split(Prefix("", condition), KeepFirst(new_???(new_???(new_???()))), body);
	return Tag(tag, Prefix(tag+"", Strip(split)));
}
/*???*/ JExpression_Lang(/*???*/ statement) {
	/*???*/ expression=new_???();
	expression.set(Or(JLambda(statement, expression), Char(), Tag("", Strip(Prefix("", First(Node("", JRules.JType()), "", Node("", expression))))), Tag("", Strip(Prefix("", Suffix(Node("", expression), "")))), Tag("", Strip(Prefix("", Node("", expression)))), StringExpr(), Switch("", expression, CaseExprValue(statement, expression)), Index(expression), NewArray(expression), Index(expression), Invokable(expression), FieldAccess(expression), MethodAccess(expression), InstanceOf(expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), Operator("", "", expression), CommonRules.Identifier(), Number()));
	/*???*/ expression;
}
/*???*/ Number_Lang() {
	return Tag("", Strip(FilterRule.Number(String(""))));
}
/*???*/ NewArray_Lang(/*???*/ expression) {
	/*???*/ type=Node("", JRules.JType());
	/*???*/ tag=Tag("", Suffix(Expressions("", expression), ""));
	/*???*/ tag1=Tag("", Node("", expression));
	/*???*/ withoutArguments=Suffix(First(type, "", Node("", tag1)), "");
	/*???*/ withArguments=Strip(First(type, "", Node("", tag)));
	return Tag("", Strip(Prefix("", Or(withoutArguments, withArguments))));
}
/*???*/ MethodAccess_Lang(/*???*/ expression) {
	/*???*/ exprSource=Tag("", Node("", expression));
	/*???*/ child=Tag("", Node("", JRules.JType()));
	return Tag("", Last(Node("", Or(exprSource, child)), "", CommonRules.StrippedIdentifier("")));
}
/*???*/ CaseExprValue_Lang(/*???*/ statement, /*???*/ expression) {
	return Or(Tag("", Node("", Strip(Suffix(expression, "")))), Tag("", Node("", statement)));
}
/*???*/ Char_Lang() {
	return Tag("", Strip(Prefix("", Suffix(String(""), ""))));
}
/*???*/ JLambda_Lang(/*???*/ statement, /*???*/ expression) {
	/*???*/ param=Tag("", CommonRules.StrippedIdentifier(""));
	/*???*/ expressions=Tag("", Expressions("", CommonRules.StrippedIdentifier("")));
	/*???*/ tag=Tag("", Empty);
	/*???*/ strip=Or(Strip(Prefix("", Suffix(Or(expressions, tag), ""))), param);
	/*???*/ child=Node("", Or(Tag("", Node("", statement)), Tag("", Node("", expression))));
	return Tag("", First(Node("", strip), "", child));
}
/*???*/ InstanceOf_Lang(/*???*/ expression) {
	/*???*/ strip=Destruct();
	/*???*/ type=Node("", Or(JRules.JDefinition(), JRules.JType(), strip));
	return Tag("", Last(Node("", expression), "", type));
}
/*???*/ Destruct_Lang() {
	return Tag("", Strip(Suffix(First(Node("", JRules.JType()), "", Parameters()), "")));
}
/*???*/ Index_Lang(/*???*/ expression) {
	return Tag("", Strip(Suffix(Last(new_???("", expression), "", new_???("", expression)), "")));
}
/*???*/ FieldAccess_Lang(/*???*/ expression) {
	/*???*/ child=Node("", expression);
	/*???*/ rightRule=CommonRules.StrippedIdentifier("");
	/*???*/ splitter=new_???("", new_???());
	return Tag("", new_???(child, rightRule, splitter, new_???()));
}
/*???*/ StringExpr_Lang() {
	return Tag("", Strip(Prefix("", Suffix(Or(String(""), Empty), ""))));
}
/*???*/ Operator_Lang(/*???*/ type, /*???*/ infix, /*???*/ expression) {
	/*???*/ left=Node("", expression);
	/*???*/ right=Node("", expression);
	/*???*/ splitter=DividingSplitter.KeepFirst(new_???(new_???(new_???(infix))));
	return Tag(type, SplitRule.Split(left, splitter, right));
}
/*???*/ Switch_Lang(/*???*/ group, /*???*/ expression, /*???*/ rule) {
	/*???*/ cases=Statements("", Strip(Or(Case(group, rule, expression), Empty)));
	/*???*/ value=Prefix("", Suffix(Node("", expression), ""));
	return Tag(""+group, Strip(Prefix("", Suffix(First(Strip(value), "", cases), ""))));
}
/*???*/ Case_Lang(/*???*/ group, /*???*/ rule, /*???*/ expression) {
	/*???*/ target=Node("", Or(JRules.JDefinition(), Destruct()));
	/*???*/ defaultCase=Strip(Prefix("", Empty));
	/*???*/ withWhen=Last(target, "", Node("", expression));
	/*???*/ value=First(Or(defaultCase, Prefix("", Or(withWhen, target))), "", Node("", rule));
	return Tag(""+group, value);
}
/*???*/ Invocation_Lang(/*???*/ expression) {
	return Invokable("", Node("", expression), expression);
}
/*???*/ Invalid_Lang() {
	return Tag("", Placeholder(String("")));
}
/*???*/ Parameters_Lang() {
	return Expressions("", Or(JRules.JDefinition(), Whitespace()));
}
