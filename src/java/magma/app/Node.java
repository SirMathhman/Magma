package magma.app;

import magma.app.maybe.MaybeString;

public interface Node {
    Node withString(String key, String value);

    MaybeString findString(String key);
}
