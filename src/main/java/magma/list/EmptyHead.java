package magma.list;

import magma.option.None;
import magma.option.Option;

public class EmptyHead<T> implements Head<T> {
	@Override
	public Option<T> next() {
		return new None<T>();
	}
}
