package magma.app.compile.error;

import magma.app.compile.node.Node;

import java.util.function.Supplier;

public sealed interface NodeResult permits NodeErr, NodeOk {
    default NodeResult merge(Supplier<NodeResult> other) {
        return switch (this) {
            case NodeErr(var error1) -> new NodeErr(error1);
            case NodeOk(var value1) -> other.get()
                    .merge0(value1);
        };
    }

    default NodeResult merge0(Node other) {
        return switch (this) {
            case NodeErr(var error) -> new NodeErr(error);
            case NodeOk(var value) -> new NodeOk(value.merge(other));
        };
    }
}
