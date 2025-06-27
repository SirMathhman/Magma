




interface Optional<T> {
	/*void ifPresent*/(Consumer<T> consumer);
	/*boolean isPresent*/();
	/*Optional<T> or*/(Supplier<Optional<T>> other);
	/*T orElseGet*/(Supplier<T> other);
	map<R>(Function<T, R> mapper) : Optional<R>;
	flatMap<R>(Function<T, Optional<R>> mapper) : Optional<R>;
	/*T orElse*/(T other);
	/*Optional<T> filter*/(Predicate<T> predicate);
	/*boolean isEmpty*/();
	/*Tuple<Boolean, T> toTuple*/(T other);
}

