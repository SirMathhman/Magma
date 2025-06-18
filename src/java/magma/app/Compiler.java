package magma.app;

import magma.api.map.MapLike;

public interface Compiler {
    String compile(MapLike<String, String> sourceMap);
}
