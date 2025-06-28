



class Some<T> {
	constructor () {
	}
	isPresent() : boolean {
	}
	or() : Optional<Some[value=]> {
	}
	orElseGet() : T {
	}
	map<Some[value=]>() : Optional<Some[value=]> {
	}
	flatMap<Some[value=]>() : Optional<Some[value=]> {
	}
	orElse() : T {
	}
	filter() : Optional<Some[value=]> {
		return /*new None<>()*/;
	}
	isEmpty() : boolean {
	}
	toTuple() : Tuple<Some[value=, T]> {
	}
	stream() : Stream<Some[value=]> {
	}
}

