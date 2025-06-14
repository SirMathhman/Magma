package magma.app.node.properties;

public interface Completer<T, N> {
    N complete(Properties<N, T> properties);
}