

class Err<Value, Error> {
	match<Return>(final whenOk : Function<Value, Return>, final whenErr : Function<Error, Return>) : Return {
		return whenErr.apply(this.error);
	}
}

