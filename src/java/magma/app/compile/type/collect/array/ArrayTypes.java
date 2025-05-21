package magma.app.compile.type.collect.array;

import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.option.Option;
import magma.app.TypeCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.compose.Composable;
import magma.app.compile.compose.StripComposable;
import magma.app.compile.compose.SuffixComposable;
import magma.app.compile.type.Type;

public class ArrayTypes {
    public static Option<Tuple2<CompileState, Type>> parseArray(CompileState state, String input) {
        return new StripComposable<Tuple2<CompileState, Type>>(new SuffixComposable<Tuple2<CompileState, Type>>("[]", (Composable<String, Tuple2<CompileState, Type>>) (String childString) -> TypeCompiler.parseType(state, childString).map(child -> {
            return new Tuple2Impl<CompileState, Type>(child.left(), new ArrayType(child.right()));
        }))).apply(input);
    }
}