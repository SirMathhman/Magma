interface ListLike<T> {
	add() : ListLike<Some[value=]>;
	popLast() : Optional<Some[value=]>;
	popFirst() : Optional<Some[value=]>;
	isEmpty() : boolean;
	contains() : boolean;
}

