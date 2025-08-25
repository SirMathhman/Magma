package magma.result;

import java.util.function.Consumer;

public sealed interface Result<Value, Error> permits Ok, Err {
	void consume(Consumer<Value> ifOk, Consumer<Error> ifError);

	boolean isErr();
}
