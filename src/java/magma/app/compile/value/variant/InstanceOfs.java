package magma.app.compile.value.variant;

import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.app.TypeCompiler;
import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.Generator;
import magma.app.compile.compose.Split;
import magma.app.compile.rule.ComposableRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.type.Type;
import magma.app.compile.value.Value;

public final class InstanceOfs {
    public record InstanceOf(Value value, Type type) implements Value {
    }

    public static Rule<Value> createRule() {
        return new ComposableRule((CompileState state) -> Split.first(" instanceof ", (String valueString, String typeString) -> ValueCompiler.createValueRule().apply(state, valueString).flatMap(valueTuple -> {
            return TypeCompiler.createTypeRule().apply(valueTuple.left(), typeString).map((Tuple2<CompileState, Type> typeTuple) -> {
                return new Tuple2Impl<CompileState, Value>(typeTuple.left(), new InstanceOf(valueTuple.right(), typeTuple.right()));
            });
        })));
    }

    public static String generate(InstanceOf instanceOf, Generator<Value> valueGenerator, Generator<Type> typeGenerator) {
        return valueGenerator.generate(instanceOf.value) + "._variant === " + typeGenerator.generate(instanceOf.type) + "._variantKey";
    }
}
