package magma.app.node;

import java.util.Optional;

public interface Node {
    Node withString(String key, String value);

    Optional<String> findString(String key);

    Node retype(String type);

    boolean is(String type);
}
