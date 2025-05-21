package magma.app.compile.type;

import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.text.Strings;
import magma.app.compile.CompileState;

public class PrimitiveTypes {
    public static Option<Tuple2<CompileState, Type>> parsePrimitive(CompileState state, String input) {
        return findPrimitiveValue(Strings.strip(input)).map((Type result) -> new Tuple2Impl<CompileState, Type>(state, result));
    }

    public static Option<Type> findPrimitiveValue(String input) {
        var stripped = Strings.strip(input);
        if (Strings.equalsTo("char", stripped) || Strings.equalsTo("Character", stripped) || Strings.equalsTo("String", stripped)) {
            return new Some<Type>(PrimitiveType.String);
        }

        if (Strings.equalsTo("int", stripped) || Strings.equalsTo("Integer", stripped)) {
            return new Some<Type>(PrimitiveType.Number);
        }

        if (Strings.equalsTo("boolean", stripped) || Strings.equalsTo("Boolean", stripped)) {
            return new Some<Type>(PrimitiveType.Boolean);
        }

        if (Strings.equalsTo("var", stripped)) {
            return new Some<Type>(PrimitiveType.Var);
        }

        if (Strings.equalsTo("void", stripped)) {
            return new Some<Type>(PrimitiveType.Void);
        }

        return new None<Type>();
    }

    public static String generatePrimitiveType(PrimitiveType primitiveType) {
        return primitiveType.value;
    }
}
