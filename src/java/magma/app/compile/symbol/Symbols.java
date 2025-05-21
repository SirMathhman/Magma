package magma.app.compile.symbol;

import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.collect.head.HeadedIter;
import magma.api.collect.head.RangeHead;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.text.Characters;
import magma.api.text.Strings;
import magma.app.compile.CompileState;
import magma.app.compile.type.resolve.ResolvedTypes;
import magma.app.compile.value.Symbol;
import magma.app.compile.type.Type;
import magma.app.compile.value.Value;

public final class Symbols {
    public static Option<Tuple2<CompileState, Type>> parseSymbolType(CompileState state, String input) {
        var stripped = Strings.strip(input);
        if (Symbols.isSymbol(stripped)) {
            var resolved = ResolvedTypes.addResolvedImportFromCache0(state, stripped);
            return new Some<Tuple2<CompileState, Type>>(new Tuple2Impl<CompileState, Type>(resolved, new Symbol(stripped)));
        }
        return new None<Tuple2<CompileState, Type>>();
    }

    public static boolean isSymbol(String input) {
        var query = new HeadedIter<Integer>(new RangeHead(Strings.length(input)));
        return query.allMatch((Integer index) -> Symbols.isSymbolChar(index, input.charAt(index)));
    }

    private static boolean isSymbolChar(int index, char c) {
        return '_' == c
                || Characters.isLetter(c)
                || (0 != index && Characters.isDigit(c));
    }

    public static Option<Tuple2<CompileState, Value>> parseSymbolValue(CompileState state, String input) {
        var stripped = Strings.strip(input);
        if (Symbols.isSymbol(stripped)) {
            var withImport = ResolvedTypes.addResolvedImportFromCache0(state, stripped);
            return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(withImport, new Symbol(stripped)));
        }
        else {
            return new None<Tuple2<CompileState, Value>>();
        }
    }

    public static String generateSymbol(Symbol symbol) {
        return symbol.value();
    }
}
