package magma.app.compile;

import jvm.list.JVMLists;
import magma.api.Tuple;
import magma.api.list.ListLike;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.compile.divide.DivideState;
import magma.app.compile.divide.MutableDivideState;
import magma.app.compile.state.CompileState;
import magma.app.compile.state.SimpleCompileState;
import magma.app.io.location.SimpleLocation;
import magma.app.io.source.Source;

import java.util.Map;

public class CompilerImpl implements Compiler {
    private static String compile(CompileState state, CharSequence input) {
        final var segments = divide(input);

        var current = state;
        final var output = new StringBuilder();
        for (var i = 0; i < segments.size(); i++) {
            final var segment = segments.get(i);
            final var maybeCompiled = compileRootSegment(segment, current);
            if (maybeCompiled.isPresent()) {
                final var compiled = maybeCompiled.get();
                current = compiled.left();
                output.append(compiled.right());
            }
        }
        return output.toString();
    }

    private static Option<Tuple<CompileState, String>> compileRootSegment(String input, CompileState state) {
        final var strip = input.strip();
        if (strip.startsWith("import ")) {
            final var withoutStart = strip.substring("import ".length());
            if (withoutStart.endsWith(";")) {
                final var withoutEnd = withoutStart.substring(0, withoutStart.length() - ";".length());
                final var separator = withoutEnd.lastIndexOf(".");
                final var parent = withoutEnd.substring(0, separator);
                final var child = withoutEnd.substring(separator + ".".length());
                return new Some<>(new Tuple<>(state.addImport(new SimpleLocation(parent, child)),
                        state.joinLocation() + " --> " + withoutEnd + "\n"));
            }
        }

        final var separator = strip.indexOf("{");
        if (separator >= 0) {
            final var beforeContent = strip.substring(0, separator);
            final var maybeStructure = compileStructureDefinition("class",
                    "class",
                    beforeContent,
                    state).or(() -> compileStructureDefinition("interface", "interface", beforeContent, state))
                    .or(() -> compileStructureDefinition("record", "class", beforeContent, state));
            if (maybeStructure.isPresent())
                return maybeStructure;
        }

        return new None<>();
    }

    private static Option<Tuple<CompileState, String>> compileStructureDefinition(String type, String type1, String input, CompileState state) {
        final var index = input.indexOf(type + " ");
        if (index >= 0) {
            final var afterKeyword = input.substring((type + " ").length() + index);
            return compileStructureDefinitionTruncated(type1, afterKeyword, state);
        }
        return new None<>();
    }

    private static Option<Tuple<CompileState, String>> compileStructureDefinitionTruncated(String type, String afterKeyword, CompileState state) {
        final var index = afterKeyword.indexOf("implements ");
        if (index >= 0) {
            final var childName = afterKeyword.substring(index + "implements ".length())
                    .strip();

            final var separator = childName.indexOf("<");
            final var trimmed = separator == -1 ? childName : childName.substring(0, separator);

            final var actual = state.find(trimmed)
                    .orElse(state.resolveSibling(trimmed));
            return generate(type, state, JVMLists.of(actual.join()));
        }
        else
            return generate(type, state, JVMLists.empty());
    }

    private static Option<Tuple<CompileState, String>> generate(String type, CompileState state, ListLike<String> superTypes) {
        final var buffer = new StringBuilder();
        for (var i = 0; i < superTypes.size(); i++) {
            final var superType = superTypes.get(i);
            buffer.append(state.joinLocation())
                    .append(" --|> ")
                    .append(superType)
                    .append("\n");
        }

        final var generated = type + " " + state.joinLocation() + "\n" + buffer;
        return new Some<>(new Tuple<>(state, generated));
    }

    private static ListLike<String> divide(CharSequence input) {
        DivideState current = new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .unwrap();
    }

    private static DivideState fold(DivideState state, char c) {
        final var appended = state.append(c);
        if (c == ';' && appended.isLevel())
            return appended.advance();
        else {
            if (c == '{')
                return appended.enter();
            if (c == '}')
                return appended.exit();
        }
        return appended;
    }

    @Override
    public String compile(Map<Source, String> sourceMap) {
        final var builder = new StringBuilder();
        for (var entry : sourceMap.entrySet()) {
            final var source = entry.getKey();
            final var location = source.computeLocation();
            builder.append(compile(new SimpleCompileState(location), entry.toString()));
        }

        return builder.toString();
    }
}
