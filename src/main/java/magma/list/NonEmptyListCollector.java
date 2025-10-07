package magma.list;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class NonEmptyListCollector<T> implements Collector<T, Option<NonEmptyList<T>>> {
	@Override
	public Option<NonEmptyList<T>> initial() {
		return new None<>();
	}

	@Override
	public Option<NonEmptyList<T>> fold(Option<NonEmptyList<T>> current, T element) {
		return new Some<>(switch (current) {
			case None<NonEmptyList<T>> v -> NonEmptyList.of(element);
			case Some<NonEmptyList<T>> v -> v.value().addLast(element);
		});
	}
}
