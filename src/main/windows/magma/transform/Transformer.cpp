// Generated transpiled C++ from 'src\main\java\magma\transform\Transformer.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Transformer {};
/*???*/ transformMethod_Transformer(/*???*/ method, /*???*/ structName) {
	List<> oldParams=/*???*/;
	List<> newParams=oldParams.stream().map(/*???*/).toList();
	/*???*/ cDefinition=transformDefinition(method.definition());
	Option<> extractedTypeParams=extractMethodTypeParameters(method);
	List<> bodySegments=/*???*/;
	return new_???(new_???(cDefinition.name()+""+structName, cDefinition.type(), cDefinition.typeParameters()), newParams, bodySegments, new_???(System.lineSeparator()), extractedTypeParams);
}
/*???*/ transformFunctionSegment_Transformer(/*???*/ segment) {
	return /*???*/;
}
/*???*/ transformWhile_Transformer(/*???*/ jWhile) {
	return new_???(transformExpression(jWhile.condition()), transformFunctionSegment(jWhile.body()));
}
/*???*/ transformAssignment_Transformer(/*???*/ jAssignment) {
	return new_???(transformExpression(jAssignment.location()), transformExpression(jAssignment.value()));
}
/*???*/ transformInitialization_Transformer(/*???*/ jInitialization) {
	return new_???(transformDefinition(jInitialization.definition()), transformExpression(jInitialization.value()));
}
/*???*/ transformBlock_Transformer(/*???*/ jBlock) {
	return new_???(jBlock.children().stream().map(/*???*/).toList());
}
/*???*/ transformIf_Transformer(/*???*/ anIf) {
	return new_???(transformExpression(anIf.condition()), transformFunctionSegment(anIf.body()));
}
/*???*/ handleConstruction_Transformer(/*???*/ jConstruction) {
	/*???*/ name=""+transformType(jConstruction.type()).stringify();
	List<> list=jConstruction.arguments().orElse(new_???()).stream().map(/*???*/).toList();
	return new_???(new_???(name), list);
}
/*???*/ transformExpression_Transformer(/*???*/ expression) {
	return /*???*/;
}
/*???*/ transformInvocation_Transformer(/*???*/ jInvocation) {
	List<> newArguments=jInvocation.arguments().orElse(new_???()).stream().map(/*???*/).toList();
	return new_???(transformExpression(jInvocation.caller()), newArguments);
}
/*???*/ transformParameter_Transformer(/*???*/ param) {
	/*???*/ transformedType=transformType(param.type());
	if (/*???*/)return new_???(param.name(), returnType, paramTypes);
	return new_???(param.name(), transformedType, new_???());
}
Option<> extractMethodTypeParameters_Transformer(/*???*/ method) {
	List<> typeVars=new_???();
	collectTypeVariables(method.definition().type(), typeVars);
	if (/*???*/)paramList.stream().forEach(/*???*/(param.type(), typeVars));
	if (typeVars.isEmpty())return new_???();
	List<> identifiers=typeVars.stream().map(/*???*/).toList();
	return new_???(identifiers);
}
/*???*/ collectTypeVariables_Transformer(/*???*/ type, List<> typeVars) {/*???*/
}
/*???*/ transformDefinition_Transformer(/*???*/ definition) {
	Option<> typeParams=definition.typeParameters();
	return new_???(definition.name(), transformType(definition.type()), typeParams);
}
List<> flattenRootSegment_Transformer(/*???*/ segment) {
	return /*???*/;
}
Result<> transform_Transformer(/*???*/ node) {
	List<> children=node.children();
	Stream<> stream=children.stream();
	Stream<> listStream=stream.map(/*???*/);
	Stream<> cRootSegmentStream=listStream.flatMap(/*???*/);
	List<> newChildren=cRootSegmentStream.toList();
	return new_???(new_???(newChildren));
}
Tuple<> flattenStructureSegment_Transformer(/*???*/ self, /*???*/ name) {
	return /*???*/;
}
List<> flattenStructure_Transformer(/*???*/ aClass) {
	List<> children=aClass.children();
	List<> recordFields=extractRecordParamsAsFields(aClass).copy();
	/*???*/ name=aClass.name();
	List<> tuples=children.stream().map(/*???*/(child, name)).toList();
	List<> segments=tuples.stream().map(/*???*/.left()).flatMap(/*???*/).toList();
	List<> additionalFields=tuples.stream().map(/*???*/.right()).filter(/*???*/).map(/*???*/(/*???*/).value()).toList();
	List<> fields=recordFields.addAll(additionalFields);
	/*???*/ structure=new_???(name, fields, new_???(System.lineSeparator()), aClass.typeParameters());
	return new_???(segments);
}
List<> extractRecordParamsAsFields_Transformer(/*???*/ structure) {
	if (/*???*/)
	{
	/*???*/();
	if (/*???*/)return paramList.stream().map(/*???*/).toList();}
	return Collections.emptyList();
}
/*???*/ transformType_Transformer(/*???*/ type) {
	return /*???*/;
}
/*???*/ transformIdentifier_Transformer(/*???*/ identifier) {
	if (identifier.value().equals(""))return new_???(new_???(""));
	/*???*/ identifier;
}
/*???*/ transformArray_Transformer(/*???*/ array) {
	/*???*/ childType=transformType(array.child());
	return new_???(childType);
}
/*???*/ transformGeneric_Transformer(/*???*/ generic) {
	List<> listOption=generic.typeArguments().orElse(new_???());
	if (generic.base().endsWith("")&&listOption.size()==/*???*/)
	{
	/*???*/ paramType=transformType(listOption.getOrNull(/*???*/));
	/*???*/ returnType=transformType(listOption.getOrNull(/*???*/));
	return new_???(returnType, List.of(paramType));}
	return new_???(generic.base().last(), listOption.stream().map(/*???*/).toList());
}
