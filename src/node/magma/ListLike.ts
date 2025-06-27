
/*import java.util.Optional;*/
/*import java.util.stream.Stream;*/
interface ListLike<T> {
	stream() : Stream<T> {}
	add(element : T) : ListLike<T> {}
	popLast() : Optional<Tuple<ListLike<T>, T>> {}
	popFirst() : Optional<Tuple<T, ListLike<T>>> {}
	/**/}

