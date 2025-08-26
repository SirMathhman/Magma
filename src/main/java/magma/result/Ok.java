package magma.result;

import java.util.function.Consumer;

public record Ok<Value, Error>(Value value) implements Result<Value, Error> {
	@Override
	public void consume(Consumer<Value> onOk, Consumer<Error> onErr) {
		onOk.accept(value);
	}

	@Override
	public boolean isErr() {
		return false;
	}
}
