package magma.app.compile.type.collect.variadic;

import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.option.Option;
import magma.api.text.Strings;
import magma.app.TypeCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.compose.SuffixComposable;
import magma.app.compile.type.Type;

public final class VariadicTypes {
    public static Option<Tuple2<CompileState, Type>> parseVariadic(CompileState state, String input) {
        var stripped = Strings.strip(input);
        return new SuffixComposable<Tuple2<CompileState, Type>>("...", (String s) -> {
            return TypeCompiler.createTypeRule().apply(state, s)
                    .map((Tuple2<CompileState, Type> tuple) -> new Tuple2Impl<CompileState, Type>(tuple.left(), tuple.right()))
                    .map((Tuple2Impl<CompileState, Type> child) -> new Tuple2Impl<CompileState, Type>(child.left(), new VariadicType(child.right())));
        }).apply(stripped);
    }

    public static String generateVariadicType(VariadicType variadicType) {
        return TypeCompiler.generateType(variadicType.type()) + "[]";
    }
}
