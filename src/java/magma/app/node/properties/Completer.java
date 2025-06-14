package magma.app.node.properties;

import magma.app.node.CompoundNode;

public interface Completer<T> {
    CompoundNode complete(Properties<CompoundNode, T> properties);
}
