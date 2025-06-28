





class None<T> {
	;
	constructor () {
	}
	ifPresent(consumer : Consumer<T>) : void {
	}
	isPresent() : boolean {
		return false;
	}
	or(other : Supplier<Optional<T>>) : Optional<T> {
		return other.get();
	}
	orElseGet(other : Supplier<T>) : T {
		return other.get();
	}
	map<R>(mapper : Function<T, R>) : Optional<R> {
		return new None();
	}
	flatMap<R>(mapper : Function<T, Optional<R>>) : Optional<R> {
		return new None();
	}
	orElse(other : T) : T {
		return other;
	}
	filter(predicate : Predicate<T>) : Optional<T> {
		return this;
	}
	isEmpty() : boolean {
		return true;
	}
	toTuple(other : T) : Tuple<Boolean, T> {
		return new Tuple(false, other);
	}
	stream() : Stream<T> {
		return Stream.empty();
	}
}

