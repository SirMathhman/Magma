




class Some<T> {
	/*@Override
    public void ifPresent*/(final Consumer<T> consumer) {
		consumer.accept(this.value);
	}
	/*@Override
    public boolean isPresent*/() {
		return true;
	}
	/*@Override
    public Optional<T> or*/(final Supplier<Optional<T>> other) {
		return this;
	}
	/*@Override
    public T orElseGet*/(final Supplier<T> other) {
		return this.value;
	}
	map<R>(final Function<T, R> mapper) : Optional<R> {
		return new Some<>(mapper.apply(this.value));
	}
	flatMap<R>(final Function<T, Optional<R>> mapper) : Optional<R> {
		return mapper.apply(this.value);
	}
	/*@Override
    public T orElse*/(final T other) {
		return this.value;
	}
	/*@Override
    public Optional<T> filter*/(final Predicate<T> predicate) {
		if (predicate.test(this.value))
			return this;
		return new None<>();
	}
	/*@Override
    public boolean isEmpty*/() {
		return false;
	}
	/*@Override
    public Tuple<Boolean, T> toTuple*/(final T other) {
		return new Tuple<>(true, this.value);
	}
}

