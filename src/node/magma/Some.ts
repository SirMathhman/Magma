




class Some<T> {
	ifPresent(final consumer : Consumer<T>) : void {
		consumer.accept(this.value);
	}
	isPresent() : boolean {
		return true;
	}
	or(final other : Supplier<Optional<T>>) : Optional<T> {
		return this;
	}
	orElseGet(final other : Supplier<T>) : T {
		return this.value;
	}
	map<R>(final mapper : Function<T, R>) : Optional<R> {
		return new Some<>(mapper.apply(this.value));
	}
	flatMap<R>(final mapper : Function<T, Optional<R>>) : Optional<R> {
		return mapper.apply(this.value);
	}
	orElse(final other : T) : T {
		return this.value;
	}
	filter(final predicate : Predicate<T>) : Optional<T> {
		if (predicate.test(this.value))
			return this;
		return new None<>();
	}
	isEmpty() : boolean {
		return false;
	}
	toTuple(final other : T) : Tuple<Boolean, T> {
		return new Tuple<>(true, this.value);
	}
}

