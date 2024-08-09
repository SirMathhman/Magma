package magma.app.compile.pass;

import magma.app.compile.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class State {
    private final List<Node> staticChildren;
    private final List<Node> instanceChildren;

    State(List<Node> staticChildren, List<Node> instanceChildren) {
        this.staticChildren = staticChildren;
        this.instanceChildren = instanceChildren;
    }

    public State() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public State addInstance(Node instanceNode) {
        var copy = new ArrayList<>(instanceChildren);
        copy.add(instanceNode);
        return new State(staticChildren, copy);
    }

    public State addStatic(Node staticNode) {
        var copy = new ArrayList<>(staticChildren);
        copy.add(staticNode);
        return new State(copy, instanceChildren);
    }

    public Optional<List<Node>> findInstanceNodes() {
        return instanceChildren.isEmpty()
                ? Optional.empty()
                : Optional.of(instanceChildren);
    }

    public Optional<List<Node>> findStaticNodes() {
        return staticChildren.isEmpty()
                ? Optional.empty()
                : Optional.of(staticChildren);
    }
}
