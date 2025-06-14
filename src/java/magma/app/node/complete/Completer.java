package magma.app.node.complete;

import magma.app.node.Properties;

public interface Completer<T, N> {
    N complete(Properties<N, T> properties);
}