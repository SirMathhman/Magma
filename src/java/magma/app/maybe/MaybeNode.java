package magma.app.maybe;

import magma.app.Generated;
import magma.app.Generator;
import magma.app.Node;

import java.util.List;

public interface MaybeNode {
    Generated generate(Generator generator);

    List<Node> addTo(List<Node> list);
}
