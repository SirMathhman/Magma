package magma.list;

public final class ArrayHead<T> implements Head<T> {
	private final T[] array;

	public ArrayHead(T[] array) {this.array = array;}
}
