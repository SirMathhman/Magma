package magma.app.map;

import jvm.map.JavaMap;

public class Maps {
    public static <Key, Value> MapLike<Key, Value> empty() {
        return new JavaMap<>();
    }
}
