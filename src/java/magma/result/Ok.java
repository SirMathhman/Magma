package magma.result;

import java.util.function.Function;

public class Ok<Value, Error> implements Result<Value, Error> {
	private final Value value;

	public Ok(final Value value) {
		this.value = value;
	}

	@Override
	public final <Return> Return match(final Function<Value, Return> whenOk, final Function<Error, Return> whenErr) {
		return whenOk.apply(this.value);
	}
}
