package magma.app.compile.node.core;

import magma.app.compile.node.properties.Properties;

import java.util.List;

public interface NodeListNode<S> {
    Properties<S, List<S>> nodeLists();
}
