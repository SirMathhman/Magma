package magma.list;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public final class ArrayHead<T> implements Head<T> {
	private final T[] array;
	private int index = 0;

	public ArrayHead(T[] array) {this.array = array;}

	@Override
	public Option<T> next() {
		if (index < array.length) {
			final T t = array[index];
			index++;
			return new Some<>(t);
		} else return new None<>();
	}
}
