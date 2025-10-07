// Generated transpiled C++ from 'src\main\java\magma\compile\CLang.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CLang {};
struct CType {};
String stringify_CType() {
}
struct CFunctionPointer {CType returnType;List<CType> paramTypes;};
String stringify_CFunctionPointer() {
	return ""+paramTypes.stream().map(/*???*/).collect(new_???(""))+""+returnType.stringify();
}
