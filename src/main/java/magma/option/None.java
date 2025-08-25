package magma.option;

import java.util.function.Function;

public record None<Value>() implements Option<Value> {

	@Override
	public boolean isNone() {
		return true;
	}

	@Override
	public <U> Option<U> map(Function<? super Value, ? extends U> f) {
		return new None<>();
	}

	@Override
	public <U> Option<U> flatMap(Function<? super Value, Option<U>> f) {
		return new None<>();
	}

	@Override
	public Value get() {
		throw new java.util.NoSuchElementException("None.get");
	}

	@Override
	public boolean isSome() {
		return !isNone();
	}
}
