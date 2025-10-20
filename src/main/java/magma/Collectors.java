package magma;

import magma.Collections.ArrayList;

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

	public static record ResultCollector<T, X, C>(Collector<T, C> collector) implements Collector<Results.Result<T, X>, Results.Result<C, X>> {
		@Override
		public Results.Result<C, X> createInitial() {
			return new Results.Ok<C, X>(this.collector.createInitial());
		}

		@Override
		public Results.Result<C, X> fold(Results.Result<C, X> current, Results.Result<T, X> element) {
			return switch (current) {
				case Results.Err<C, X> v -> new Results.Err<C, X>(v.error());
				case Results.Ok<C, X> v -> switch (element) {
					case Results.Err<T, X> v1 -> new Results.Err<C, X>(v1.error());
					case Results.Ok<T, X> v1 -> new Results.Ok<C, X>(this.collector.fold(v.value(), v1.value()));
				};
			};
		}
	}
}
