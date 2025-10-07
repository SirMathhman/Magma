// Generated transpiled C++ from 'src\main\java\magma\transform\Transformer.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Transformer {};
CFunction transformMethod_Transformer() {
	Option<NonEmptyList<JDefinition>> maybeOldParams=method.params();
	Option<NonEmptyList<CParameter>> newParams=maybeOldParams.flatMap();
	CDefinition cDefinition=transformDefinition();
	Option<NonEmptyList<Identifier>> extractedTypeParams=extractMethodTypeParameters();
	NonEmptyList<CFunctionSegment> bodySegments=method.body().map().orElse();
	return new_???();
}
NonEmptyList<CFunctionSegment> getCFunctionSegmentNonEmptyList_Transformer() {
	return body.stream().map().collect().orElse();
}
CFunctionSegment transformFunctionSegment_Transformer() {
	return /*???*/;
}
CWhile transformWhile_Transformer() {
	return new_???();
}
CAssignment transformAssignment_Transformer() {
	return new_???();
}
CInitialization transformInitialization_Transformer() {
	return new_???();
}
CBlock transformBlock_Transformer() {
	return new_???();
}
CIf transformIf_Transformer() {
	CFunctionSegment body=transformFunctionSegment();
	CBlock record;
	if (/*???*/)
	{
	record=b;}
	else record=new_???();
	return new_???();
}
CInvocation handleConstruction_Transformer() {
	String name=""+transformType().stringify();
	Option<NonEmptyList<CExpression>> list=jConstruction.arguments().flatMap();
	return new_???();
}
CExpression transformExpression_Transformer() {
	return /*???*/;
}
CInvocation transformInvocation_Transformer() {
	Option<NonEmptyList<CExpression>> newArguments=jInvocation.arguments().flatMap();
	return new_???();
}
Option<NonEmptyList<CExpression>> transformExpressionList_Transformer() {
	return list.stream().map().collect();
}
CParameter transformParameter_Transformer() {
	CType transformedType=transformType();
	if (/*???*/)
	{
	return new_???();}
	return new_???();
}
Option<NonEmptyList<Identifier>> extractMethodTypeParameters_Transformer() {
	List<String> typeVars=new_???();
	collectTypeVariables();
	if (/*???*/)
	{
	paramList.stream().forEach();}
	if (typeVars.isEmpty())
	{
	return new_???();}
	return typeVars.stream().map().collect();
}
void collectTypeVariables_Transformer() {/*???*/
}
CDefinition transformDefinition_Transformer() {
	Option<List<Identifier>> typeParams=definition.typeParameters();
	return new_???();
}
List<CRootSegment> flattenRootSegment_Transformer() {
	return /*???*/;
}
Result<CRoot, CompileError> transform_Transformer() {
	List<JavaRootSegment> children=node.children();
	Stream<JavaRootSegment> stream=children.stream();
	Stream<List<CRootSegment>> listStream=stream.map();
	Stream<CRootSegment> cRootSegmentStream=listStream.flatMap();
	List<CRootSegment> newChildren=cRootSegmentStream.toList();
	return new_???();
}
Tuple<List<CRootSegment>, Option<CDefinition>> flattenStructureSegment_Transformer() {
	return /*???*/;
}
List<CRootSegment> flattenStructure_Transformer() {
	List<JStructureSegment> children=aClass.children();
	List<CDefinition> recordFields=extractRecordParamsAsFields().copy();
	String name=aClass.name();
	List<Tuple<List<CRootSegment>, Option<CDefinition>>> tuples=children.stream().map().toList();
	List<CRootSegment> segments=tuples.stream().map().flatMap().toList();
	List<CDefinition> additionalFields=tuples.stream().map().filter().map().toList();
	List<CDefinition> fields=recordFields.addAll();
	CStructure structure=new_???();
	return new_???();
}
List<CDefinition> extractRecordParamsAsFields_Transformer() {
	if (/*???*/)
	{
	/*???*/();
	if (/*???*/)
	{
	return paramList.stream().map().toList();}}
	return Collections.emptyList();
}
CType transformType_Transformer() {
	return /*???*/;
}
CType transformIdentifier_Transformer() {
	if (identifier.value().equals())
	{
	return new_???();}
	return identifier;
}
Pointer transformArray_Transformer() {
	CType childType=transformType();
	return new_???();
}
CType transformGeneric_Transformer() {
	List<JType> listOption=generic.typeArguments().orElse();
	if (generic.base().endsWith()&&listOption.size()==/*???*/)
	{
	CType paramType=transformType();
	CType returnType=transformType();
	return new_???();}
	List<CType> transformedTypes=listOption.stream().map().toList();
	return NonEmptyList.fromList().map().orElse();
}
