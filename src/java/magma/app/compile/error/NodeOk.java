package magma.app.compile.error;

import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.State;

import java.util.List;
import java.util.function.Function;

public record NodeOk(NodeWithEverything node) implements NodeResult<NodeWithEverything> {
    @Override
    public NodeResult<NodeWithEverything> transform(Function<NodeWithEverything, NodeResult<NodeWithEverything>> mapper) {
        return mapper.apply(this.node);
    }

    @Override
    public StringResult generate(Function<NodeWithEverything, StringResult> generator) {
        return generator.apply(this.node);
    }

    @Override
    public State<NodeWithEverything> attachToState(State<NodeWithEverything> state) {
        return state.withValue(this.node);
    }

    @Override
    public Result<List<NodeWithEverything>, CompileError> attachToList(List<NodeWithEverything> list) {
        list.add(this.node);
        return new Ok<>(list);
    }
}
