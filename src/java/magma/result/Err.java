package magma.result;

import java.util.function.Function;

public class Err<Value, Error> implements Result<Value, Error> {
	private final Error error;

	public Err(final Error error) {
		this.error = error;
	}

	@Override
	public final <Return> Return match(final Function<Value, Return> whenOk, final Function<Error, Return> whenErr) {
		return whenErr.apply(this.error);
	}
}