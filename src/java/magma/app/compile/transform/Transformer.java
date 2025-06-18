package magma.app.compile.transform;

import magma.app.compile.node.NodeWithEverything;

public interface Transformer {
    NodeWithEverything transform(NodeWithEverything root, String name);
}
