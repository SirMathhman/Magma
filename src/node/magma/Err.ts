

class Err<Value, Error> {
	match<Return>(final Function<Value, Return> whenOk, final Function<Error, Return> whenErr) : Return {
		return whenErr.apply(this.error);
	}
}

