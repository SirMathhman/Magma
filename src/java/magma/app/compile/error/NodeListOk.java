package magma.app.compile.error;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.app.compile.node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record NodeListOk(List<Node> node) implements NodeListResult {
    public NodeListOk() {
        this(new ArrayList<>());
    }

    @Override
    public NodeListResult add(Supplier<NodeResult<Node>> action) {
        return switch (action.get()
                .appendTo(this.node)) {
            case Err<List<Node>, FormattedError>(var error) -> new NodeListErr(error);
            case Ok<List<Node>, FormattedError>(var value) -> new NodeListOk(value);
        };
    }
}
