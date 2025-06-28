

class Ok<Value, Error> {
	value : Value;
	constructor (value : Value) {
		this.value = value;
	}
	match<Return>(whenOk : Function<Value, Return>, whenErr : Function<Error, Return>) : Return {
		return whenOk.apply(this.value);
	}
}

