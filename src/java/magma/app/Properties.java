package magma.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Properties {
    private final Map<String, String> map;
    private final Completer completer;

    public Properties(Map<String, String> map, Completer completer) {
        this.map = map;
        this.completer = completer;
    }

    public Properties(Completer completer) {
        this(new HashMap<>(), completer);
    }

    public Node withString(String key, String value) {
        this.map.put(key, value);
        return this.completer.complete(this);
    }

    public Optional<String> findString(String key) {
        if (this.map.containsKey(key))
            return Optional.of(this.map.get(key));

        return Optional.empty();
    }
}