package magma.app.compile.type.resolve.template;

import magma.api.Tuple2;
import magma.api.collect.Joiner;

public final class FunctionTypes {
    public static String generateFunctionType(FunctionType functionType) {
        var joinedArguments = functionType.args()
                .iterWithIndices()
                .map((Tuple2<Integer, String> tuple) -> "arg" + tuple.left() + " : " + tuple.right())
                .collect(new Joiner(", "))
                .orElse("");

        return "(" + joinedArguments + ") => " + functionType.returns();
    }
}
