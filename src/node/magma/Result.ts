

interface Result<Value, Error> {
	match(whenOk : Function<Value, Return>, whenErr : Function<Error, Return>) : Return {}
	}

