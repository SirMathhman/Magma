package magma.app.compile;

import magma.api.collect.iter.Collector;

import java.util.Map;

public record StringNodeCollector(Node node) implements Collector<Map.Entry<String, String>, Node> {
    @Override
    public Node createInitial() {
        return this.node;
    }

    @Override
    public Node fold(Node current, Map.Entry<String, String> entry) {
        return current.withString(entry.getKey(), entry.getValue());
    }
}
