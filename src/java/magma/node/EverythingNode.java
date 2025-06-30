package magma.node;

import magma.api.Tuple;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface EverythingNode extends TypedNode<EverythingNode> {
    Stream<Tuple<String, String>> streamStrings();

    EverythingNode withString(String key, String value);

    Optional<String> findString(String key);

    EverythingNode merge(EverythingNode other);

    EverythingNode withNodeList(String key, List<EverythingNode> values);

    Optional<List<EverythingNode>> findNodeList(String key);
}
