




class None<T> {
	ifPresent(final consumer : Consumer<T>) : void {
	}
	isPresent() : boolean {
		return false;
	}
	or(final other : Supplier<Optional<T>>) : Optional<T> {
		return other.get();
	}
	orElseGet(final other : Supplier<T>) : T {
		return other.get();
	}
	map<R>(final mapper : Function<T, R>) : Optional<R> {
		return new None<>();
	}
	flatMap<R>(final mapper : Function<T, Optional<R>>) : Optional<R> {
		return new None<>();
	}
	orElse(final other : T) : T {
		return other;
	}
	filter(final predicate : Predicate<T>) : Optional<T> {
		return this;
	}
	isEmpty() : boolean {
		return true;
	}
	toTuple(final other : T) : Tuple<Boolean, T> {
		return new Tuple<>(false, other);
	}
}

