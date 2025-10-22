package magma;

import magma.Collections.ArrayList;
import magma.Collections.ListMap;
import magma.Results.Err;
import magma.Results.Ok;
import magma.Results.Result;
import magma.Utils.Tuple;

import java.util.function.Predicate;

public class Collectors {
	public interface Collector<T, C> {
		C createInitial();

		C fold(C current, T element);
	}

	public static class ListCollector<T> implements Collector<T, ArrayList<T>> {
		public ListCollector() {}

		@Override
		public ArrayList<T> createInitial() {
			return new ArrayList<T>();
		}

		@Override
		public ArrayList<T> fold(ArrayList<T> current, T element) {
			return current.addLast(element);
		}
	}

	public static class Joiner implements Collector<String, String> {
		private final String delimiter;

		public Joiner(String delimiter) {this.delimiter = delimiter;}

		public Joiner() {
			this("");
		}

		@Override
		public String createInitial() {
			return "";
		}

		@Override
		public String fold(String current, String element) {
			if (current.isEmpty()) {
				return element;
			}
			return current + this.delimiter + element;
		}
	}

	public record AnyMatch<T>(Predicate<T> predicate) implements Collector<T, Boolean> {
		@Override
		public Boolean createInitial() {
			return false;
		}

		@Override
		public Boolean fold(Boolean current, T element) {
			return current || this.predicate.test(element);
		}
	}

	public record ResultCollector<T, X, C>(Collector<T, C> collector) implements Collector<Result<T, X>, Result<C, X>> {
		@Override
		public Result<C, X> createInitial() {
			return new Ok<C, X>(this.collector.createInitial());
		}

		@Override
		public Result<C, X> fold(Result<C, X> current, Result<T, X> element) {
			return switch (current) {
				case Err<C, X> v -> new Err<C, X>(v.error());
				case Ok<C, X> v -> switch (element) {
					case Err<T, X> v1 -> new Err<C, X>(v1.error());
					case Ok<T, X> v1 -> new Ok<C, X>(this.collector.fold(v.value(), v1.value()));
				};
			};
		}
	}

	public record AllMatch<T>(Predicate<T> predicate) implements Collector<T, Boolean> {
		@Override
		public Boolean createInitial() {
			return true;
		}

		@Override
		public Boolean fold(Boolean current, T element) {
			return current && this.predicate.test(element);
		}
	}

	public static class MapCollector<K, V> implements Collector<Tuple<K, V>, ListMap<K, V>> {
		@Override
		public ListMap<K, V> createInitial() {
			return new ListMap<K, V>();
		}

		@Override
		public ListMap<K, V> fold(ListMap<K, V> current, Tuple<K, V> element) {
			return current.put(element.left(), element.right());
		}
	}
}
