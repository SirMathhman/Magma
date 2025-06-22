package magma.node;

import magma.option.Option;

public interface EverythingNode extends TypedNode<EverythingNode>, DisplayNode {
    EverythingNode withString(String key, String value);

    Option<String> findString(String key);
}
