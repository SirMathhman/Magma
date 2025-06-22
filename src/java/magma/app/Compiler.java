package magma.app;

import magma.api.list.ListLike;
import magma.api.list.ListLikes;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.divide.DivideState;
import magma.app.divide.MutableDivideState;
import magma.app.error.FormattedError;
import magma.app.node.EverythingNode;
import magma.app.node.result.NodeErr;
import magma.app.node.result.NodeOk;
import magma.app.string.StringErr;
import magma.app.string.StringOk;
import magma.app.string.StringResult;

import java.util.Map;

public class Compiler {
    private Compiler() {
    }

    public static Result<String, FormattedError> compileEntryToResult(final Map<String, String> inputs) {
        return switch (Compiler.compileEntry(inputs)) {
            case StringErr(final var error) -> new Err<>(error);
            case StringOk(final var value) -> new Ok<>(value);
        };
    }

    private static StringResult<FormattedError> compileEntry(final Map<String, String> inputs) {
        StringResult<FormattedError> maybeCompiled = new StringOk<>();
        for (final var source : inputs.entrySet()) {
            final var name = source.getKey();
            final var input = source.getValue();

            maybeCompiled = maybeCompiled.tryAppendResult(() -> Compiler.compile(input, name));
        }

        return maybeCompiled;
    }

    private static StringResult<FormattedError> compile(final CharSequence input, final String name) {
        final var segments = Compiler.divide(input);
        return segments.stream()
                .<StringResult<FormattedError>>reduce(new StringOk<>(),
                        (output, segment) -> Compiler.getStringResult(name, output, segment),
                        (_, next) -> next)
                .prepend("class " + name + Lang.SEPARATOR);
    }

    private static ListLike<String> divide(final CharSequence input) {
        final var segments = ListLikes.<String>empty();
        final var buffer = new StringBuilder();
        final var depth = 0;
        return Compiler.getStringListLike(input, new MutableDivideState(segments, buffer, depth));
    }

    private static ListLike<String> getStringListLike(final CharSequence input, final DivideState state) {
        var current = state;
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Compiler.fold(current, c);
        }

        return current.advance()
                .toList();
    }

    private static DivideState fold(final DivideState current, final char c) {
        final var appended = current.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();

        if ('{' == c)
            return appended.enter();

        if ('}' == c)
            return appended.exit();

        return appended;
    }

    private static StringResult<FormattedError> getStringResult(final String name, final StringResult<FormattedError> output, final String segment) {
        final var tree = Lang.createRootSegmentRule()
                .lex(segment);

        final var generated = (Result<Option<StringResult<FormattedError>>, FormattedError>) switch (tree) {
            case NodeErr(final var error1) -> new Err<>(error1);
            case NodeOk(final EverythingNode value1) -> new Ok<>(Compiler.transformAndGenerate(name, value1));
        };

        return switch (generated) {
            case Err<Option<StringResult<FormattedError>>, FormattedError>(final var error) -> new StringErr<>(error);
            case Ok<Option<StringResult<FormattedError>>, FormattedError>(final var value) ->
                    value instanceof Some(final var result) ? output.appendResult(result) : output;
        };
    }

    private static Option<StringResult<FormattedError>> transformAndGenerate(final String name, final EverythingNode destination) {
        final var node = destination.withString("source", name);
        final var destination1 = node.findString("destination")
                .orElse("");

        if (Compiler.isFunctionalInterface(destination1))
            return new None<>();

        if (node.is("import"))
            return new Some<>(Compiler.generate(node.retype("dependency")));
        else
            return new None<>();
    }

    private static boolean isFunctionalInterface(final String destination) {
        return ListLikes.of("Consumer", "Function", "Supplier")
                .contains(destination);
    }

    private static StringResult<FormattedError> generate(final EverythingNode node) {
        return Lang.createPlantUMLRootSegmentRule()
                .generate(node);
    }
}
