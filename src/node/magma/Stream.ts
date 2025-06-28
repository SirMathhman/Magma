

interface Stream<Value> {
	fromArray<Some[value=]>() : Stream<Some[value=]> {
	}
	empty<Some[value=]>() : Stream<Some[value=]> {
	}
	map() : Stream<Some[value=]>;
	collect() : C;
	flatMap() : Stream<Some[value=]>;
	filter() : Stream<Some[value=]>;
	next() : Optional<Some[value=]>;
}

