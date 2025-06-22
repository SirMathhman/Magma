package magma.node;

import magma.option.Option;

public interface Node {
    Node withString(String key, String value);

    Option<String> findString(String key);

    String display();
}
