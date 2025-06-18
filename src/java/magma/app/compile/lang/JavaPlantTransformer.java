package magma.app.compile.lang;

import magma.api.collect.list.Lists;
import magma.app.compile.lang.segment.JavaPlantRootSegmentTransformer;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.transform.Transformer;

public class JavaPlantTransformer implements Transformer {
    @Override
    public NodeWithEverything transform(NodeWithEverything root, String name) {
        var newChildren = root.findNodeList("children")
                .orElse(Lists.empty())
                .fold(Lists.<NodeWithEverything>empty(),
                        (list, root1) -> list.addAll(JavaPlantRootSegmentTransformer.modifyRootSegment(name, root1)));

        return new MapNode().withNodeList("children", newChildren);
    }
}