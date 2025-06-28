

class Ok<Value, Error> {
	value : Value;
	constructor (value : Value) {
	}
	match<Return>(whenOk : Function<Value, Return>, whenErr : Function<Error, Return>) : Return {
		return whenOk.apply(this.value);
	}
}

