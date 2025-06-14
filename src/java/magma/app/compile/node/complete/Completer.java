package magma.app.compile.node.complete;

import magma.app.compile.node.Properties;

public interface Completer<T, N> {
    N complete(Properties<N, T> properties);
}