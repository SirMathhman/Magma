// Generated transpiled C++ from 'src\main\java\magma\compile\Lang.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Lang {};
struct JavaRootSegment {};
struct CRootSegment {};
Option<String> after_CRootSegment() {/*???*/
}
struct JStructureSegment {};
struct JExpression {};
struct JMethodSegment {};
struct CFunctionSegment {};
struct JType {};
struct JStructure {};
String name_JStructure() {/*???*/
}
Option<NonEmptyList<Identifier>> typeParameters_JStructure() {/*???*/
}
List<JStructureSegment> children_JStructure() {/*???*/
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
struct CharNode {};
struct CAnd {};
struct And {};
struct Destruct {};
struct InstanceOf {};
struct Wildcard {};
struct JAdd {};
struct JSubtract {};
struct JEquals {};
struct JNotEquals {};
struct CEquals {};
struct JString {};
struct CAdd {};
struct CString {};
struct JFieldAccess {};
struct CFieldAccess {};
struct JConstruction {};
struct JInvocation {};
struct Not {};
struct ExprCaseExprValue {};
struct StatementCaseExprValue {};
record CaseExpr_Lang() {
}
record CaseStatement_Lang() {
}
struct SwitchExpr {};
struct SwitchStatement {};
struct ExprLambdaValue {};
struct StatementLambdaValue {};
struct Lambda {};
struct LengthNewArrayValue {};
struct ArgumentsNewArrayValue {};
struct NewArray {};
struct CAssignment {};
struct CPostFix {};
struct JAssignment {};
struct JPostFix {};
struct JInitialization {};
struct CInitialization {};
struct CBlock {};
struct JBlock {};
struct JIf {};
struct CIf {};
struct JWhile {};
struct CWhile {};
struct Field {};
struct JGeneric {};
struct CTemplate {};
String stringify_CTemplate() {
	return base+""+typeArguments.stream().map().collect();
}
struct Array {};
struct JDefinition {};
record Modifier_Lang() {
}
struct JMethod {};
struct Invalid {};
public Invalid_Invalid() {
	this();
}
String stringify_Invalid() {
	return "";
}
struct JClass {};
struct Interface {};
struct RecordNode {};
struct CStructure {};
struct Whitespace {};
struct Placeholder {};
record JRoot_Lang() {
}
record CRoot_Lang() {
}
struct Import {};
struct Package {};
struct CDefinition {};
struct CFunctionPointerDefinition {};
struct CFunction {};
struct Identifier {};
String stringify_Identifier() {
	return value;
}
struct Pointer {};
String stringify_Pointer() {
	return child.stringify()+"";
}
struct LineComment {};
struct BlockComment {};
struct JReturn {};
struct CReturn {};
struct JElse {};
struct CElse {};
struct CInvocation {};
struct Break {};
struct InvocationFolder {};
DivideState fold_InvocationFolder() {
	DivideState appended=state.append();
	if (c==open)
	{
	DivideState enter=appended.enter();
	if (enter.isShallow())
	{
	return enter.advance();}
	else
	return enter;}
	if (c==close)
	{
	return appended.exit();}
	return appended;
}
String delimiter_InvocationFolder() {
	return "";
}
struct Index {};
struct Quantity {};
struct JLessThanEquals {};
struct JGreaterThan {};
struct JOr {};
struct JGreaterThanEquals {};
struct JLessThan {};
struct Try {};
struct Catch {};
struct Yield {};
struct Variadic {};
struct MyFolder {};
DivideState fold_MyFolder() {
	if (c=='(')
	{
	return state.append().enter();}
	if (c==')')
	{
	if (state.isLevel())
	{
	return state.advance();}}
	else
	return state.exit().append();
	return state.append();
}
String delimiter_MyFolder() {
	return "";
}
struct EmptyLambdaParam {};
struct SingleLambdaParam {};
struct MultipleLambdaParam {};
struct ExprMethodAccessSource {};
struct TypeMethodAccessSource {};
struct MethodAccess {};
struct NumberNode {};
struct OperatorFolder {};
DivideState fold_OperatorFolder() {
	if (c==operator.charAt())
	{
	if (/*???*/)
	{
	state.pop();
	return state.advance();}
	return state.advance();}
	return state.append();
}
String delimiter_OperatorFolder() {
	return operator;
}
record QualifiedSegment_Lang() {
}
struct JQualified {};
String last_JQualified() {
	if (/*???*/)
	{
	return "";}
	return segments.orElse().getLast().map().orElse();
}
boolean endsWith_JQualified() {
	return /*???*/;
}
List<String> unwrap_JQualified() {
	if (/*???*/)
	{
	return list.stream().map().toList();}
	return new_???();
}
Rule CFunctionPointerDefinition_Lang() {
	return Tag();
}
Rule CDefinition_Lang() {
	return Last();
}
Rule CType_Lang() {
	LazyRule rule=new_???();
	Rule funcPtr=Tag();
	rule.set();
	return rule;
}
Rule CTemplate_Lang() {
	Rule base=String();
	Rule arguments=Or();
	return Tag();
}
Rule CStructure_Lang() {
	Rule plainName=CommonRules.StrippedIdentifier();
	Rule structPrefix=Prefix();
	Rule fields=Or();
	Rule structWithFields=Suffix();
	Rule structComplete=Suffix();
	Rule templateParams=Expressions();
	Rule templateDecl=Prefix();
	Rule maybeTemplate=new_???();
	return Tag();
}
Rule JRoot_Lang() {
	Rule segment=Or();
	return Statements();
}
Rule Structures_Lang() {
	return Or();
}
Rule Whitespace_Lang() {
	return Tag();
}
Rule Namespace_Lang() {
	return Tag();
}
Rule JStructure_Lang() {
	Rule modifiers=String();
	Rule maybeWithTypeArguments=NameWithTypeParameters();
	Rule maybeWithParameters=Strip();
	Rule maybeWithParameters1=Or();
	Rule beforeContent=Or();
	Rule children=Statements();
	Rule beforeContent1=Or();
	Rule strip=Strip();
	Rule first=First();
	Rule aClass=Split();
	return Tag();
}
Rule NameWithTypeParameters_Lang() {
	Rule name=CommonRules.StrippedIdentifier();
	Rule withTypeParameters=Suffix();
	return Strip();
}
Rule JStructureSegment_Lang() {
	LazyRule structureMember=new_???();
	structureMember.set();
	return structureMember;
}
Rule BlockComment_Lang() {
	return Tag();
}
Rule LineComment_Lang() {
	return Tag();
}
Rule Statement_Lang() {
	Rule initialization=Initialization();
	return Strip();
}
Rule JMethod_Lang() {
	Rule params=Parameters();
	Rule header=Strip();
	Rule withBody=Suffix();
	return Tag();
}
Rule JMethodSegment_Lang() {
	LazyRule methodSegment=new_???();
	Rule expression=JExpression();
	Rule inner=JRules.JDefinition();
	methodSegment.set();
	return methodSegment;
}
Rule Try_Lang() {
	Rule child=Node();
	Rule definition=JRules.JDefinition();
	Rule value=JExpression();
	Rule definition1=Node();
	Rule value1=Node();
	Rule resource=Node();
	Splitter splitter=new_???();
	Rule withResource=new_???();
	ContextRule withoutResource=new_???();
	return Tag();
}
Rule Block_Lang() {
	return Tag();
}
Rule JMethodStatementValue_Lang() {
	Rule expression=JExpression();
	return Or();
}
Rule Break_Lang() {
	return Tag();
}
Rule PostFix_Lang() {
	return Tag();
}
Rule Initialization_Lang() {
	Rule definition1=Node();
	Rule value1=Node();
	return First();
}
Rule Invokable_Lang() {
	return Or();
}
Rule Invokable_Lang() {
	Rule arguments=Or();
	FoldingDivider divider=new_???();
	Rule suffix=Strip();
	return Tag();
}
Rule Yield_Lang() {
	return Tag();
}
Rule Return_Lang() {
	return Tag();
}
Rule Else_Lang() {
	return Tag();
}
Rule Conditional_Lang() {
	return QuantityBlock();
}
Rule QuantityBlock_Lang() {
	Rule condition=Node();
	Rule body=Node();
	Rule split=Split();
	return Tag();
}
Rule JExpression_Lang() {
	LazyRule expression=new_???();
	expression.set();
	return expression;
}
Rule Number_Lang() {
	return Tag();
}
Rule NewArray_Lang() {
	Rule type=Node();
	Rule tag=Tag();
	Rule tag1=Tag();
	Rule withoutArguments=Suffix();
	Rule withArguments=Strip();
	return Tag();
}
Rule MethodAccess_Lang() {
	Rule exprSource=Tag();
	Rule child=Tag();
	return Tag();
}
Rule CaseExprValue_Lang() {
	return Or();
}
Rule Char_Lang() {
	return Tag();
}
Rule JLambda_Lang() {
	Rule param=Tag();
	Rule expressions=Tag();
	Rule tag=Tag();
	Rule strip=Or();
	Rule child=Node();
	return Tag();
}
Rule InstanceOf_Lang() {
	Rule strip=Destruct();
	Rule type=Node();
	return Tag();
}
Rule Destruct_Lang() {
	return Tag();
}
Rule Index_Lang() {
	return Tag();
}
Rule FieldAccess_Lang() {
	Rule child=Node();
	Rule rightRule=CommonRules.StrippedIdentifier();
	Splitter splitter=new_???();
	return Tag();
}
Rule StringExpr_Lang() {
	return Tag();
}
Rule Operator_Lang() {
	Rule left=Node();
	Rule right=Node();
	Splitter splitter=DividingSplitter.KeepFirst();
	return Tag();
}
Rule Switch_Lang() {
	Rule cases=Statements();
	Rule value=Prefix();
	return Tag();
}
Rule Case_Lang() {
	Rule target=Node();
	Rule defaultCase=Strip();
	Rule withWhen=Last();
	Rule value=First();
	return Tag();
}
Rule Invocation_Lang() {
	return Invokable();
}
Rule Invalid_Lang() {
	return Tag();
}
Rule Parameters_Lang() {
	return Expressions();
}
