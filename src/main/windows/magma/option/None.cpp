struct None<T> implements Option<T>{};
/*Option_?*/ map_None<T> implements Option<T>(R mapper) {/*
		return new None<>();
	*/}
/*Option_?*/ flatMap_None<T> implements Option<T>(OptionR mapper) {/*
		return new None<>();
	*/}
T orElse_None<T> implements Option<T>(T other) {/*
		return other;
	*/}
