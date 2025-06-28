

interface Result<Value, Error> {
	match<Return>(whenOk : Function<Value, Return>, whenErr : Function<Error, Return>) : Return;
}

