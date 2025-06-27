

interface Result<Value, Error> {
	match<Return>(Function<Value, Return> whenOk, Function<Error, Return> whenErr) : Return;
}

