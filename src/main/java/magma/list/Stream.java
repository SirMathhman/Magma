package magma.list;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Stream<T> {
	static Stream<Integer> range(int start, int end) {
		return new HeadedStream<>(new RangeHead(end - start)).map(offset -> start + offset);
	}

	<R> Stream<R> map(Function<T, R> mapper);

	<R> R fold(R initial, BiFunction<R, T, R> folder);

	<R> R collect(Collector<T, R> collector);

	default List<T> toList() {
		return collect(new ListCollector<T>());
	}

	void forEach(Consumer<T> consumer);

	<R> Stream<R> flatMap(Function<T, Stream<R>> mapper);

	Stream<T> filter(Predicate<T> predicate);

	default <R> R reduce(R initial, BiFunction<R, T, R> folder) {
		return fold(initial, folder);
	}

	boolean allMatch(Predicate<T> predicate);

	boolean anyMatch(Predicate<T> predicate);
}
