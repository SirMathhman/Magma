package magma.app;

import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.text.Strings;
import magma.app.compile.CompileState;
import magma.app.compile.rule.Rule;
import magma.app.compile.text.Whitespace;

public final class WhitespaceCompiler {
    private static class WhitespaceRule implements Rule<String> {
        @Override
        public Option<Tuple2<CompileState, String>> apply(CompileState state, String input) {
            return WhitespaceCompiler.parseWhitespace(state, input).map((Tuple2<CompileState, Whitespace> tuple) -> new Tuple2Impl<CompileState, String>(tuple.left(), tuple.right().generate()));
        }
    }

    public static Rule<String> createWhitespaceRule() {
        return new WhitespaceRule();
    }

    static Option<Tuple2<CompileState, Whitespace>> parseWhitespace(CompileState state, String input) {
        if (Strings.isBlank(input)) {
            return new Some<Tuple2<CompileState, Whitespace>>(new Tuple2Impl<CompileState, Whitespace>(state, new Whitespace()));
        }
        return new None<Tuple2<CompileState, Whitespace>>();
    }
}