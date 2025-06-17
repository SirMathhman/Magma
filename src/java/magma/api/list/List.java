package magma.api.list;

public interface List<T> extends Iterable<T> {
    List<T> add(T element);
}
