package magma.result;

import java.util.function.Function;

public interface Result<Value, Error> {
	<Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenErr);
}
