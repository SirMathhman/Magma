




interface Optional<T> {
	constructor () {
	}
	ifPresent(consumer : Consumer<T>) : void;
	isPresent() : boolean;
	or(other : Supplier<Optional<T>>) : Optional<T>;
	orElseGet(other : Supplier<T>) : T;
	map<R>(mapper : Function<T, R>) : Optional<R>;
	flatMap<R>(mapper : Function<T, Optional<R>>) : Optional<R>;
	orElse(other : T) : T;
	filter(predicate : Predicate<T>) : Optional<T>;
	isEmpty() : boolean;
	toTuple(other : T) : Tuple<Boolean, T>;
}

