

interface Result<Value, Error> {
	constructor () {
	}
	match<Return>(whenOk : Function<Value, Return>, whenErr : Function<Error, Return>) : Return;
}

