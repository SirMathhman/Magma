struct Ok<T, X>(T value) implements Result<T, X>{};
/*X>*/ mapValue_Ok<T, X>(T value) implements Result<T, X>(R fn) {/*
		return new Ok<>(fn.apply(this.value));
	*/}
/*X>*/ flatMap_Ok<T, X>(T value) implements Result<T, X>(X fn) {/*
		return fn.apply(this.value);
	*/}
/*R>*/ mapErr_Ok<T, X>(T value) implements Result<T, X>(R mapper) {/*
		return new Ok<>(value);
	*/}
