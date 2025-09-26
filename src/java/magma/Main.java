package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Main {
	private interface Collector<T, C> {
		C createInitial();

		C fold(C current, T element);
	}

	private interface Stream<T> {
		<R> Stream<R> map(Function<T, R> mapper);

		<C> C collect(Collector<T, C> collector);
	}

	private interface List<T> {
		Stream<T> stream();

		List<T> add(T element);
	}

	private interface Head<T> {
		Optional<T> next();
	}

	private record HeadedStream<T>(Head<T> head) implements Stream<T> {
		@Override
		public <R> Stream<R> map(Function<T, R> mapper) {
			return new HeadedStream<>(() -> head.next().map(mapper));
		}

		@Override
		public <C> C collect(Collector<T, C> collector) {
			return this.fold(collector.createInitial(), collector::fold);
		}

		private <C> C fold(C initial, BiFunction<C, T, C> folder) {
			C current = initial;
			while (true) {
				C finalCurrent = current;
				final Optional<C> folded = head.next().map(next -> folder.apply(finalCurrent, next));
				if (folded.isPresent()) current = folded.get();
				else return current;
			}
		}
	}

	private static final class ArrayHead<T> implements Head<T> {
		private final T[] array;
		private final int size;
		private int counter = 0;

		private ArrayHead(T[] array, int size) {
			this.array = array;
			this.size = size;
		}

		@Override
		public Optional<T> next() {
			if (counter >= size) return Optional.empty();
			final T element = array[counter];
			counter++;
			return Optional.of(element);
		}
	}

	private record ArrayList<T>(T[] array, int size) implements List<T> {
		public ArrayList() {
			//noinspection unchecked
			this((T[]) new Object[10], 0);
		}

		@Override
		public Stream<T> stream() {
			return new HeadedStream<>(new ArrayHead<>(array, size));
		}

		@Override
		public List<T> add(T element) {
			return set(size, element);
		}

		private List<T> set(int index, T element) {
			final T[] capacity = resize(index);
			final int newSize = Math.max(size, index + 1);
			capacity[index] = element;
			return new ArrayList<>(capacity, newSize);
		}

		private T[] resize(int index) {
			int oldCapacity = array.length;
			if (index < oldCapacity) return array;

			int newCapacity = oldCapacity;
			while (!(index < newCapacity)) newCapacity *= 2;

			final Object[] destination = new Object[newCapacity];
			System.arraycopy(array, 0, destination, 0, oldCapacity);
			//noinspection unchecked
			return (T[]) destination;
		}
	}

	private static class State {
		private List<String> segments = new ArrayList<>();
		private StringBuilder buffer = new StringBuilder();
		private int depth = 0;

		private boolean isLevel() {
			return depth == 0;
		}

		private boolean isShallow() {
			return depth == 1;
		}

		private Stream<String> stream() {
			return segments.stream();
		}

		private State exit() {
			this.depth = depth - 1;
			return this;
		}

		private State enter() {
			this.depth = depth + 1;
			return this;
		}

		private State advance() {
			this.segments = segments.add(buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		private State append(char c) {
			buffer.append(c);
			return this;
		}
	}

	private static class Joiner implements Collector<String, Optional<String>> {
		@Override
		public Optional<String> createInitial() {
			return Optional.empty();
		}

		@Override
		public Optional<String> fold(Optional<String> current, String element) {
			return Optional.of(current.map(inner -> inner + element).orElse(element));
		}
	}

	public static void main(String[] args) {
		try {
			final String input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "src", "node", "magma", "Main.ts"), compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(String input) {
		return compileStatements(input, Main::compileRootSegment);
	}

	private static String compileStatements(String input, Function<String, String> mapper) {
		return divide(input).map(mapper).collect(new Joiner()).orElse("");
	}

	private static String compileRootSegment(String input) {
		final String strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return compileClass(strip, 0).orElseGet(() -> wrap(strip));
	}

	private static Optional<String> compileClass(String input, int depth) {
		final int i = input.indexOf("class ");
		if (i < 0) return Optional.empty();
		final String modifiers = input.substring(0, i);
		final String afterKeyword = input.substring(i + "class ".length());

		final int i1 = afterKeyword.indexOf("{");
		if (i1 < 0) return Optional.empty();
		final String name = afterKeyword.substring(0, i1).strip();
		final String substring = afterKeyword.substring(i1 + "{".length()).strip();

		if (!substring.endsWith("}")) return Optional.empty();
		final String content = substring.substring(0, substring.length() - 1);
		return Optional.of(wrap(modifiers) + "class " + name + " {" +
											 compileStatements(content, input1 -> compileClassSegment(input1, depth + 1)) + "}");
	}

	private static String compileClassSegment(String input, int depth) {
		final String strip = input.strip();
		return System.lineSeparator() + "\t".repeat(depth) + compileClassSegmentValue(depth, strip);
	}

	private static String compileClassSegmentValue(int depth, String input) {
		return compileField(input).orElseGet(() -> compileClass(input, depth).orElseGet(() -> wrap(input)));
	}

	private static Optional<String> compileField(String input) {
		if (!input.endsWith(";")) return Optional.empty();
		final String substring = input.substring(0, input.length() - ";".length());

		final int i = substring.indexOf("=");
		if (i < 0) return Optional.empty();
		final String substring1 = substring.substring(0, i);
		final String substring2 = substring.substring(i + "=".length());

		return Optional.of(compileDefinition(substring1) + " = " + wrap(substring2) + ";");
	}

	private static String compileDefinition(String input) {
		final String strip = input.strip();

		final int i = strip.lastIndexOf(" ");
		if (i < 0) return wrap(strip);
		final String beforeName = strip.substring(0, i);
		final String name = strip.substring(i + " ".length());

		final int i1 = beforeName.lastIndexOf(" ");
		if (i1 < 0) return wrap(strip);
		final String beforeType = beforeName.substring(0, i1);
		final String type = beforeName.substring(i1 + " ".length());

		return wrap(beforeType) + name + " : " + wrap(type);
	}

	private static Stream<String> divide(String input) {
		State current = new State();
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			current = fold(current, c);
		}

		return current.advance().stream();
	}

	private static State fold(State state, char c) {
		final State appended = state.append(c);
		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '}' && appended.isShallow()) return appended.advance().exit();
		if (c == '{') return appended.enter();
		if (c == '}') return appended.exit();
		return appended;
	}

	private static String wrap(String input) {
		final String replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}
