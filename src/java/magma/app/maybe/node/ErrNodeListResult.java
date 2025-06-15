package magma.app.maybe.node;

import magma.app.CompileError;
import magma.app.maybe.NodeListResult;
import magma.app.maybe.NodeResult;
import magma.app.maybe.StringResult;
import magma.app.maybe.string.ErrStringResult;

import java.util.List;
import java.util.function.Function;

public class ErrNodeListResult<Node> implements NodeListResult<Node> {
    private final CompileError error;

    public ErrNodeListResult(CompileError error) {
        this.error = error;
    }

    @Override
    public NodeListResult<Node> add(NodeResult<Node> node) {
        return this;
    }

    @Override
    public NodeListResult<Node> transform(Function<List<Node>, List<Node>> mapper) {
        return this;
    }

    @Override
    public StringResult generate(Function<List<Node>, StringResult> generator) {
        return new ErrStringResult(this.error);
    }
}
