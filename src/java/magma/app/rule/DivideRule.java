package magma.app.rule;

import magma.app.node.MapNode;
import magma.app.node.Node;
import magma.app.rule.divide.DivideState;
import magma.app.rule.divide.MutableDivideState;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record DivideRule(String key, Rule rule) implements Rule {
    public static List<String> divide(String input) {
        DivideState current = new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments().toList();
    }

    private static DivideState fold(DivideState state, char c) {
        final var appended = state.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }

    @Override
    public LexResult lex(String input) {
        final var children = divide(input).stream().map(segment -> this.rule.lex(segment).maybeValue()).flatMap(Optional::stream).toList();
        return LexResult.of(new MapNode().withNodeList(this.key, children));
    }

    @Override
    public GenerationResult generate(Node node) {
        return GenerationResult.of(node.findNodeList(this.key()).orElse(new ArrayList<>()).stream().map(source -> this.rule().generate(source).value()).flatMap(Optional::stream).collect(Collectors.joining()));
    }
}