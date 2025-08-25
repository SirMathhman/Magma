package magma.option;

import java.util.function.Consumer;

public record None<Value>() implements Option<Value> {
	@Override
	public void consume(Consumer<Value> ifSome, Runnable ifNone) {
		ifNone.run();
	}

	@Override
	public boolean isNone() {
		return true;
	}
}
