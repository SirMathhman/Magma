struct Some<T>(T value) implements Option<T>{};
Option<R> map_Some<T>(T value) implements Option<T>(R mapper) {/*
		return new Some<>(mapper.apply(value));
	*/}
Option<R> flatMap_Some<T>(T value) implements Option<T>(OptionR mapper) {/*
		return mapper.apply(value);
	*/}
T orElse_Some<T>(T value) implements Option<T>(T other) {/*
		return value;
	*/}
