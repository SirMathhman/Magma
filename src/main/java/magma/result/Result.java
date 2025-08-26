package magma.result;

import java.util.function.Consumer;

public interface Result<Value, Error> {
	void consume(Consumer<Value> onOk, Consumer<Error> onErr);

	boolean isErr();
}
