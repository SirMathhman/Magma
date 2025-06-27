

class Err<Value, Error> {
	match(whenOk : Function<Value, Return>, whenErr : Function<Error, Return>) : Return {
		return whenErr.apply(this.error);}
}

