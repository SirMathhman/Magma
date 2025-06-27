

class Ok<Value, Error> {
	match<Return>(final Function<Value, Return> whenOk, final Function<Error, Return> whenErr) : Return {
		return whenOk.apply(this.value);
	}
}

