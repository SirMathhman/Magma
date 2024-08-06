package magma.app.compile.pass;

import magma.api.Tuple;
import magma.app.compile.Node;
import magma.app.compile.Passer;
import magma.app.compile.lang.common.Blocks;
import magma.app.compile.lang.common.PrefixedStatements;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static magma.app.compile.lang.common.Blocks.CHILDREN;
import static magma.app.compile.lang.common.Declarations.AFTER_DEFINITION;
import static magma.app.compile.lang.common.Declarations.BEFORE_VALUE;
import static magma.app.compile.lang.common.Declarations.DECLARATION;
import static magma.app.compile.lang.common.PrefixedStatements.BEFORE_BLOCK;

public class MagmaFormatter implements Passer {
    private static Optional<Tuple<Node, Integer>> postVisitTry(Node node, int state) {
        if (node.is(PrefixedStatements.TRY)) return Optional.of(new Tuple<>(node.withString(BEFORE_BLOCK, " "), state));
        return Optional.empty();
    }

    private static Optional<Tuple<Node, Integer>> postVisitBlock(Node node, int state) {
        if (!node.is(Blocks.BLOCK)) return Optional.empty();

        var blockIndent = state <= 0 ? "" : "\t".repeat(state - 1);
        var newNode = node
                .mapNodeList(CHILDREN, oldChildren -> attachAllIndent(state, oldChildren))
                .withString(Blocks.AFTER_CHILDREN, "\n" + blockIndent);

        return Optional.of(new Tuple<>(newNode, state - 1));
    }

    private static List<Node> attachAllIndent(int state, List<Node> children) {
        return IntStream.range(0, children.size())
                .mapToObj(index -> attachIndent(state, index, children))
                .toList();
    }

    private static Node attachIndent(int state, int index, List<Node> children) {
        var newChild = children.get(index);
        if (index == 0 && state == 0) return newChild;

        var indent = state < 0 ? "\n" : "\n" + "\t".repeat(state);
        return newChild.withString(Blocks.BEFORE_CHILD, indent);
    }

    @Override
    public Optional<Tuple<Node, Integer>> preVisit(Node node, int state) {
        if (node.is(Blocks.BLOCK)) {
            return Optional.of(new Tuple<>(node, state + 1));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Tuple<Node, Integer>> postVisit(Node node, int state) {
        return postVisitTry(node, state)
                .or(() -> postVisitBlock(node, state))
                .or(() -> postVisitDeclaration(node, state));
    }

    private Optional<Tuple<Node, Integer>> postVisitDeclaration(Node node, int state) {
        if (!node.is(DECLARATION)) return Optional.empty();

        var newNode = node
                .withString(AFTER_DEFINITION, " ")
                .withString(BEFORE_VALUE, " ");

        return Optional.of(new Tuple<>(newNode, state));
    }
}
