

class Err<Value, Error> {
	error : Error;
	match<Return>(whenOk : Function<Value, Return>, whenErr : Function<Error, Return>) : Return {
		return whenErr.apply(this.error);
	}
}

