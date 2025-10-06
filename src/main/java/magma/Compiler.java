package magma;

import magma.compile.Lang;
import magma.compile.Serializers;
import magma.compile.error.CompileError;
import magma.result.Result;
import magma.transform.RootTransformer;

import static magma.compile.Lang.CRoot;
import static magma.compile.Lang.JRoot;

public class Compiler {
	public static Result<String, CompileError> compile(String input) {
		return JRoot().lex(input)
									.flatMap(node -> Serializers.deserialize(JRoot.class, node))
									.flatMap(RootTransformer::transform)
									.flatMap(cRoot -> Serializers.serialize(Lang.CRoot.class, cRoot))
									.flatMap(CRoot()::generate);
	}

}
