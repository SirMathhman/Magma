package magma.app.node.core;

import magma.app.node.properties.Properties;

import java.util.List;

public interface NodeListNode<S> {
    Properties<S, List<S>> nodeLists();
}
