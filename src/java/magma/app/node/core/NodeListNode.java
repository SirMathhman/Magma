package magma.app.node.core;

import magma.app.node.Properties;

import java.util.List;

public interface NodeListNode<S> {
    Properties<S, List<S>> nodeLists();
}
