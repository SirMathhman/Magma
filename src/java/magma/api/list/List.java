package magma.api.list;

public interface List<T> extends Streamable<T> {
    List<T> add(T element);
}
