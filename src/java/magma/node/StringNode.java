package magma.node;

import magma.option.Option;

public interface StringNode<Self> {
    Self withString(String key, String value);

    Option<String> findString(String key);
}
