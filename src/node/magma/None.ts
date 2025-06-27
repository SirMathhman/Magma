




class None<T> {
	/*@Override
    public void ifPresent*/(final Consumer<T> consumer) {
	}
	/*@Override
    public boolean isPresent*/() {
		return false;
	}
	/*@Override
    public Optional<T> or*/(final Supplier<Optional<T>> other) {
		return other.get();
	}
	/*@Override
    public T orElseGet*/(final Supplier<T> other) {
		return other.get();
	}
	map<R>(final Function<T, R> mapper) : Optional<R> {
		return new None<>();
	}
	flatMap<R>(final Function<T, Optional<R>> mapper) : Optional<R> {
		return new None<>();
	}
	/*@Override
    public T orElse*/(final T other) {
		return other;
	}
	/*@Override
    public Optional<T> filter*/(final Predicate<T> predicate) {
		return this;
	}
	/*@Override
    public boolean isEmpty*/() {
		return true;
	}
	/*@Override
    public Tuple<Boolean, T> toTuple*/(final T other) {
		return new Tuple<>(false, other);
	}
}

