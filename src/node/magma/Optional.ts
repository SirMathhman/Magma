




interface Optional<T> {
	isPresent() : boolean;
	or() : Optional<Some[value=]>;
	orElseGet() : T;
	map() : Optional<Some[value=]>;
	flatMap() : Optional<Some[value=]>;
	orElse() : T;
	filter() : Optional<Some[value=]>;
	isEmpty() : boolean;
	toTuple() : Tuple<Some[value=, T]>;
	stream() : Stream<Some[value=]>;
}

