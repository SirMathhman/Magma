

class Err<Value, Error> {
	error : Error;
	constructor (error : Error) {
		this.error = error;
	}
	match<Return>(whenOk : Function<Value, Return>, whenErr : Function<Error, Return>) : Return {
		return whenErr.apply(this.error);
	}
}

