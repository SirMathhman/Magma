package magma.app.compile.rule.extract;

import magma.api.Result;
import magma.api.collect.fold.Folding;
import magma.api.collect.list.ListLike;
import magma.api.collect.list.Lists;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.action.CompileError;
import magma.app.compile.rule.action.Generator;
import magma.app.compile.rule.action.Lexer;
import magma.app.compile.rule.divide.DivideState;
import magma.app.compile.rule.divide.MutableDivideState;

import java.util.Optional;

public class NodeListExtractor<Node extends NodeWithNodeLists<Node>, Rule extends Lexer<Node, Result<Node, CompileError>> & Generator<Node, Result<String, CompileError>>> implements
        Extractor<Node, ListLike<Node>> {
    private final Rule rule;

    public NodeListExtractor(Rule rule) {
        this.rule = rule;
    }

    public static Folding<String> divide(CharSequence input) {
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
    public Node attach(Node node, String key, ListLike<Node> values) {
        return node.withNodeList(key, values);
    }

    @Override
    public Optional<ListLike<Node>> lex(String input) {
        return Optional.of(divide(input).fold(Lists.empty(),
                (current, segment) -> (this.rule).lex(segment)
                        .findValue()
                        .map(current::add)
                        .orElse(current)));
    }

    @Override
    public Optional<ListLike<Node>> fromNode(Node node, String key) {
        return node.findNodeList(key);
    }

    @Override
    public Optional<String> generate(ListLike<Node> children) {
        final var output = children.fold(new StringBuilder(),
                (current, node) -> this.rule.generate(node)
                        .findValue()
                        .map(current::append)
                        .orElse(current));

        return Optional.of(output.toString());
    }
}
