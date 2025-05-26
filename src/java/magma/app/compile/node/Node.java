package magma.app.compile.node;

import magma.api.collect.list.List;
import magma.api.option.None;
import magma.api.option.Option;

public interface Node {
    default Option<Node> findNode(String key) {
        return new None<>();
    }

    default Option<String> findString(String key) {
        return new None<>();
    }

    default Node withNode(String key, Node value) {
        return this;
    }

    default Node withString(String key, String value) {
        return this;
    }

    default Option<List<Node>> findNodeList(String key) {
        return new None<>();
    }

    boolean is(String type);

    default Node retype(String type) {
        return this;
    }

    default Node withNodeList(String key, List<Node> values) {
        return this;
    }
}
