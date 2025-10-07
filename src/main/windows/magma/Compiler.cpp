// Generated transpiled C++ from 'src\main\java\magma\Compiler.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Compiler {};
Result</*???*/, /*???*/> compile_Compiler(/*???*/ input) {
	return JRoot().lex(input).flatMap(/*???*/.deserialize(JRoot.class, node)).flatMap(/*???*/).flatMap(/*???*/.serialize(Lang.CRoot.class, cRoot)).flatMap(/*???*/);
}
