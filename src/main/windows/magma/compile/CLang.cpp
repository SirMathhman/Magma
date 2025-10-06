// Generated transpiled C++ from 'src\main\java\magma\compile\CLang.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CLang {};
struct CType {};
char* stringify_CType() {
}
struct CFunctionPointer {CType returnType;List<> paramTypes;};
char* stringify_CFunctionPointer() {
	return ""+paramTypes.stream().map(/*???*/).collect(Collectors.joining(""))+""+returnType.stringify();
}
