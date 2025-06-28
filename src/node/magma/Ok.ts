

class Ok<Value, Error> {
	value : Value;
	match<Return>(whenOk : Function<Value, Return>, whenErr : Function<Error, Return>) : Return {
		return whenOk.apply(this.value);
	}
}

