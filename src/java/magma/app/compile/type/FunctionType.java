package magma.app.compile.type;

import magma.api.collect.list.Iterable;
import magma.app.compile.node.Node;

import java.util.Objects;

public final class FunctionType implements Node {
    private final Iterable<Node> args;
    private final Node returns;

    private FunctionType(Iterable<Node> args, Node returns) {
        this.args = args;
        this.returns = returns;
    }

    public static FunctionType createFunctionType(Iterable<Node> args, Node returns) {
        return new FunctionType(args, returns);
    }

    public boolean is(String type) {
        return "functional".equals(type);
    }

    public Iterable<Node> args() {
        return args;
    }

    public Node returns() {
        return returns;
    }
}
