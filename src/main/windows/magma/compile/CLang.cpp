// Generated transpiled C++ from 'src\main\java\magma\compile\CLang.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CLang {};
struct CType {};
/*???*/ stringify_CType() {
}
struct CFunctionPointer {/*???*/ returnType;List<> paramTypes;};
/*???*/ stringify_CFunctionPointer() {
	return ""+paramTypes.stream().map(/*???*/).collect(new_???(""))+""+returnType.stringify();
}
