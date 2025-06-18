package magma.app.compile.rule;

import magma.api.collect.fold.Foldable;
import magma.api.collect.list.Lists;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.divide.DivideState;
import magma.app.compile.rule.divide.MutableDivideState;

import java.util.Optional;

public final class DivideRule<Node extends NodeWithNodeLists<Node>> implements Rule<Node> {
    private final String key;
    private final Rule<Node> rule;
    private final NodeFactory<Node> nodeFactory;

    public DivideRule(String key, Rule<Node> rule, NodeFactory<Node> nodeFactory) {
        this.key = key;
        this.rule = rule;
        this.nodeFactory = nodeFactory;
    }

    public static Foldable<String> divide(CharSequence input) {
        DivideState current = new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static DivideState fold(DivideState current, char c) {
        final var appended = current.append(c);
        if (c == ';' && appended.isLevel())
            return appended.advance();
        if (c == '{')
            return appended.enter();
        if (c == '}')
            return appended.exit();
        return appended;
    }

    @Override
    public Optional<Node> lex(String input) {
        final var children = divide(input).fold(Lists.<Node>empty(),
                (current, segment) -> DivideRule.this.rule.lex(segment)
                        .map(current::add)
                        .orElse(current));

        return Optional.of(this.nodeFactory.create()
                .withNodeList(this.key, children));
    }

    @Override
    public Optional<String> generate(Node root) {
        final var output = root.findNodeList(this.key)
                .orElse(Lists.empty())
                .fold(new StringBuilder(),
                        (current, node) -> this.rule.generate(node)
                                .map(current::append)
                                .orElse(current));

        return Optional.of(output.toString());
    }
}