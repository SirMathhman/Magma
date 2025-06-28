





class Some<T> {
	value : T;
	constructor (value : T) {
		this.value = value;
	}
	ifPresent(consumer : Consumer<T>) : void {
		consumer.accept(this.value);
	}
	isPresent() : boolean {
		return true;
	}
	or(other : Supplier<Optional<T>>) : Optional<T> {
		return this;
	}
	orElseGet(other : Supplier<T>) : T {
		return this.value;
	}
	map<R>(mapper : Function<T, R>) : Optional<R> {
		return new Some(mapper.apply(this.value));
	}
	flatMap<R>(mapper : Function<T, Optional<R>>) : Optional<R> {
		return mapper.apply(this.value);
	}
	orElse(other : T) : T {
		return this.value;
	}
	filter(predicate : Predicate<T>) : Optional<T> {
		if (predicate.test(this.value))
			return this;
		return new None();
	}
	isEmpty() : boolean {
		return false;
	}
	toTuple(other : T) : Tuple<Boolean, T> {
		return new Tuple(true, this.value);
	}
	stream() : Stream<T> {
		return Stream.of(value);
	}
}

