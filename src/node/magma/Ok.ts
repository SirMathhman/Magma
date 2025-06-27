

class Ok<Value, Error> {
	public match<Return>(final whenOk : Function<Value, Return>, final whenErr : Function<Error, Return>) : Return {
		return whenOk.apply(this.value);
	}
}

