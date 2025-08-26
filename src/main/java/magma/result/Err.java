package magma.result;

import java.util.function.Consumer;

public record Err<Value, Error>(Error error) implements Result<Value, Error> {
	@Override
	public void consume(Consumer<Value> onOk, Consumer<Error> onErr) {
		onErr.accept(error);
	}

	@Override
	public boolean isErr() {
		return true;
	}
}
