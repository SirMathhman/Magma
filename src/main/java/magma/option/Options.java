package magma.option;

import magma.list.ArrayHead;
import magma.list.EmptyHead;
import magma.list.Head;
import magma.list.HeadedStream;
import magma.list.Stream;

public class Options {
	public static <T> Stream<T> stream(Option<T> option) {
		return new HeadedStream<T>(createHead(option));
	}

	private static <T> Head<T> createHead(Option<T> option) {
		return switch (option) {
			case None<T> _ -> new EmptyHead<T>();
			case Some<T> v -> new ArrayHead<T>(v.value());
		};
	}
}
