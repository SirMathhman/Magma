struct Ok<>{};
/*X>*/ mapValue_Ok(R fn) {/*
		return new Ok<>(fn.apply(this.value));
	*/}
/*X>*/ flatMap_Ok(X fn) {/*
		return fn.apply(this.value);
	*/}
/*R>*/ mapErr_Ok(R mapper) {/*
		return new Ok<>(value);
	*/}
