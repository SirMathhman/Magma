package magma.option;

import java.util.function.Consumer;

public sealed interface Option<Value> permits Some, None {
	void consume(Consumer<Value> ifSome, Runnable ifNone);

	boolean isNone();
}
