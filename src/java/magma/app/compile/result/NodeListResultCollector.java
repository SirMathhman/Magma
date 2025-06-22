package magma.app.compile.result;

import magma.api.collect.stream.Collector;

public class NodeListResultCollector implements Collector<NodeResult, NodeListResult> {
    @Override
    public NodeListResult createInitial() {
        return new NodeListOk();
    }

    @Override
    public NodeListResult fold(final NodeListResult current, final NodeResult nodeResult) {
        return current.addResult(nodeResult);
    }
}
