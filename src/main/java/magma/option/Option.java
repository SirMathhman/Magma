package magma.option;

import java.util.function.Function;

public sealed interface Option<Value> permits Some, None {
	boolean isNone();

	boolean isSome();

	<U> Option<U> map(Function<? super Value, ? extends U> f);

	<U> Option<U> flatMap(Function<? super Value, Option<U>> f);

	/*
	Prefer something like this:

	```
	Option<?> value = ???;
	if(value instanceof Some(var value)) {
	}
	```

	or use `match`.
	 */
	@Deprecated
	Value get();
}
