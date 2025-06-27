




class Some<T> {
	public ifPresent(final consumer : Consumer<T>) : void {
		consumer.accept(this.value);
	}
	public isPresent() : boolean {
		return true;
	}
	public or(final other : Supplier<Optional<T>>) : Optional<T> {
		return this;
	}
	public orElseGet(final other : Supplier<T>) : T {
		return this.value;
	}
	public map<R>(final mapper : Function<T, R>) : Optional<R> {
		return new Some<>(mapper.apply(this.value));
	}
	public flatMap<R>(final mapper : Function<T, Optional<R>>) : Optional<R> {
		return mapper.apply(this.value);
	}
	public orElse(final other : T) : T {
		return this.value;
	}
	public filter(final predicate : Predicate<T>) : Optional<T> {
		if (predicate.test(this.value))
			return this;
		return new None<>();
	}
	public isEmpty() : boolean {
		return false;
	}
	public toTuple(final other : T) : Tuple<Boolean, T> {
		return new Tuple<>(true, this.value);
	}
}

