package magma.app.maybe;

import magma.app.Generated;
import magma.app.Generator;
import magma.app.Node;

import java.util.List;
import java.util.stream.Stream;

public interface MaybeNode {
    MaybeNode withString(String key, String value);

    Generated generate(Generator generator);

    Stream<Node> stream();

    List<Node> addTo(List<Node> list);
}
