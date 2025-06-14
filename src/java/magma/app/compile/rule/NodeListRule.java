package magma.app.compile.rule;

import magma.app.compile.node.core.NodeListNode;
import magma.app.compile.rule.divide.DivideState;
import magma.app.compile.rule.divide.MutableDivideState;
import magma.app.compile.rule.factory.NodeFactory;
import magma.app.compile.rule.result.GenerationResult;
import magma.app.compile.rule.result.LexResult;
import magma.app.compile.rule.result.optional.OptionalGenerationResult;
import magma.app.compile.rule.result.optional.OptionalLexResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class NodeListRule<N extends NodeListNode<N>> implements Rule<N> {
    private final String key;
    private final Rule<N> rule;
    private final NodeFactory<N> factory;

    public NodeListRule(String key, Rule<N> rule, NodeFactory<N> factory) {
        this.key = key;
        this.rule = rule;
        this.factory = factory;
    }

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
    public LexResult<N> lex(String input) {
        final var children = divide(input).stream().map(segment -> this.rule.lex(segment).findValue()).flatMap(Optional::stream).toList();
        return OptionalLexResult.of(this.factory.create().nodeLists().with(this.key, children));
    }

    @Override
    public GenerationResult generate(N node) {
        return OptionalGenerationResult.of(node.nodeLists().find(this.key).orElse(new ArrayList<>()).stream().map(source -> this.rule.generate(source).findValue()).flatMap(Optional::stream).collect(Collectors.joining()));
    }
}