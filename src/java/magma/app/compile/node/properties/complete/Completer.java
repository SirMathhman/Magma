package magma.app.compile.node.properties.complete;

import magma.app.compile.node.properties.Properties;

public interface Completer<T, N> {
    N complete(Properties<N, T> properties);
}
