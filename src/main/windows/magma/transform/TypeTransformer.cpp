// Generated transpiled C++ from 'src\main\java\magma\transform\TypeTransformer.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct TypeTransformer {};
/*???*/ transformType_TypeTransformer(/*???*/ type) {
	return /*???*/;
}
/*???*/ transformIdentifier_TypeTransformer(/*???*/ identifier) {
	if (identifier.value().equals(""))return new_???(new_???(""));
	return identifier;
}
/*???*/ transformArray_TypeTransformer(/*???*/ array) {
	/*???*/ childType=transformType(array.child());
	return new_???(childType);
}
/*???*/ transformGeneric_TypeTransformer(/*???*/ generic) {
	List<> listOption=generic.typeArguments().orElse(new_???());
	if (generic.base().equals("")&&listOption.size()==/*???*/)
	{
	/*???*/ paramType=transformType(listOption.get(/*???*/));
	/*???*/ returnType=transformType(listOption.get(/*???*/));
	return new_???(returnType, List.of(paramType));}
	return new_???(generic.base(), listOption.stream().map(/*???*/).toList());
}
