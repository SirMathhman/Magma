




class None<T> {
	public ifPresent(final consumer : Consumer<T>) : void {
	}
	public isPresent() : boolean {
		return false;
	}
	public or(final other : Supplier<Optional<T>>) : Optional<T> {
		return other.get();
	}
	public orElseGet(final other : Supplier<T>) : T {
		return other.get();
	}
	public map<R>(final mapper : Function<T, R>) : Optional<R> {
		return new None<>();
	}
	public flatMap<R>(final mapper : Function<T, Optional<R>>) : Optional<R> {
		return new None<>();
	}
	public orElse(final other : T) : T {
		return other;
	}
	public filter(final predicate : Predicate<T>) : Optional<T> {
		return this;
	}
	public isEmpty() : boolean {
		return true;
	}
	public toTuple(final other : T) : Tuple<Boolean, T> {
		return new Tuple<>(false, other);
	}
}

