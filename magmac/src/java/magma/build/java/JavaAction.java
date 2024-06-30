package magma.build.java;

public interface JavaAction<T, E extends Throwable> {
    T execute() throws E;
}
