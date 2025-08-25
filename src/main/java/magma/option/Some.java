package magma.option;

import java.util.function.Function;

public record Some<Value>(Value value) implements Option<Value> {

	@Override
	public boolean isNone() {
		return false;
	}

	@Override
	public <U> Option<U> map(Function<? super Value, ? extends U> f) {
		return new Some<>(f.apply(value));
	}

	@Override
	public <U> Option<U> flatMap(Function<? super Value, Option<U>> f) {
		return f.apply(value);
	}

	@Override
	public Value get() {
		return value;
	}

	@Override
	public boolean isSome() {
		return !isNone();
	}
}
