// Generated transpiled C++ from 'src\main\java\magma\compile\CNodes.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CNodes {};
struct CType {};
String stringify_CType() {/*???*/
}
struct CFunctionPointer {};
String stringify_CFunctionPointer() {
	return ""+paramTypes.stream().map().collect()+""+returnType.stringify();
}
struct Cast {};
