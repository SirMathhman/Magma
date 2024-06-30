package magma.build.compile.lang.magma;

import magma.api.Tuple;
import magma.api.contain.List;
import magma.api.contain.stream.Streams;
import magma.api.option.Options;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.lang.Visitor;
import magma.build.compile.error.Error_;
import magma.build.compile.annotate.State;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.rule.text.StripRule;
import magma.build.java.JavaList;

public class BlockFormatter implements Visitor {
    private static List<Node> indentChildren(List<Node> children, State state) {
        return Streams.rangeTo(children.size())
                .extend(children::get)
                .map(Options::invertRight)
                .flatMap(Streams::fromOption)
                .map(child -> attachAfterChild(child, state))
                .collect(JavaList.collecting());
    }

    private static Node attachAfterChild(Tuple<Integer, Node> child, State state) {
        var depth = state.computeDepth();
        var childIndent = "\n" + "\t".repeat(depth - 1);

        var index = child.left();
        if (index == 0 && depth == 1) return child.right();

        return child.right().withString(StripRule.DEFAULT_LEFT, childIndent);
    }

    private static Node attachAfterChildren(Node node, int depth) {
        if (depth <= 1) return node;

        var count = depth - 2;
        var endIndent = "\n" + "\t".repeat(count);
        return node.withString("after-children", endIndent);
    }

    @Override
    public Result<Tuple<Node, State>, Error_> preVisit(Node node, State state) {
        return new Ok<>(new Tuple<>(node, state.enter()));
    }

    @Override
    public Result<Tuple<Node, State>, Error_> postVisit(Node node, State state) {
        var withChildren = node.mapNodes("children", children -> indentChildren(children, state));
        var exited = state.exit();

        var depth = state.computeDepth();
        var withAfterChildren = attachAfterChildren(withChildren, depth);
        var tuple = new Tuple<>(withAfterChildren, exited);
        return new Ok<>(tuple);
    }
}
