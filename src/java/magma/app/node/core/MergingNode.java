package magma.app.node.core;

import magma.app.node.CompoundNode;

public interface MergingNode {
    CompoundNode merge(CompoundNode other);
}
