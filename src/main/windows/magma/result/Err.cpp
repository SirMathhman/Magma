struct Err<>{};
/*X>*/ mapValue_Err(R fn) {/*
		return new Err<>(error);
	*/}
/*X>*/ flatMap_Err(X fn) {/*
		return new Err<>(error);
	*/}
/*R>*/ mapErr_Err(R mapper) {/*
		return new Err<>(mapper.apply(error));
	*/}
