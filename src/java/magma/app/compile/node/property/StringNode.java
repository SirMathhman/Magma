package magma.app.compile.node.property;

import magma.api.option.Option;

public interface StringNode<Self> {
    Self withString(String key, String value);

    Option<String> findString(String key);
}
