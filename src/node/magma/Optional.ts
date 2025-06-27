




interface Optional<T> {
	of(value : T) : Optional<T> {
		return new Some<>(value);
	}
	empty() : Optional<T> {
		return new None<>();
	}
	ifPresent(consumer : Consumer<T>) : void;
	isPresent() : boolean;
	or(other : Supplier<Optional<T>>) : Optional<T>;
	orElseGet(other : Supplier<T>) : T;
	map(mapper : Function<T, R>) : Optional<R>;
	flatMap(mapper : Function<T, Optional<R>>) : Optional<R>;
	orElse(other : T) : T;
	filter(predicate : Predicate<T>) : Optional<T>;
	isEmpty() : boolean;
	toTuple(other : T) : Tuple<Boolean, T>;
}

