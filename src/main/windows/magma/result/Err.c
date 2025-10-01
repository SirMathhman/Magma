struct Err<T, X>(X error) implements Result<T, X>{};
/*X>*/ mapValue_Err<T, X>(X error) implements Result<T, X>(R fn) {/*
		return new Err<>(error);
	*/}
/*X>*/ flatMap_Err<T, X>(X error) implements Result<T, X>(X fn) {/*
		return new Err<>(error);
	*/}
/*R>*/ mapErr_Err<T, X>(X error) implements Result<T, X>(R mapper) {/*
		return new Err<>(mapper.apply(error));
	*/}
