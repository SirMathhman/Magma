package magma.option;

import java.util.function.Consumer;

public record Some<Value>(Value value) implements Option<Value> {
	@Override
	public void consume(Consumer<Value> ifSome, Runnable ifNone) {
		ifSome.accept(value);
	}

	@Override
	public boolean isNone() {
		return false;
	}
}
