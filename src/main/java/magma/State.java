package magma;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class State {
	public final String input;
	public final ArrayList<String> segments;
	private StringBuilder buffer;
	private int depth;
	private int index = 0;

	public State(String input) {
		this.input = input;
		this.buffer = new StringBuilder();
		this.depth = 0;
		this.segments = new ArrayList<>();
	}

	State enter() {
		this.depth = this.depth + 1;
		return this;
	}

	State exit() {
		this.depth = this.depth - 1;
		return this;
	}

	State advance() {
		this.segments.add(this.buffer.toString());
		this.buffer = new StringBuilder();
		return this;
	}

	boolean isShallow() {
		return this.depth == 1;
	}

	State append(char c) {
		this.buffer.append(c);
		return this;
	}

	boolean isLevel() {
		return this.depth == 0;
	}

	public Optional<Character> pop() {
		if (this.index < this.input.length()) {
			var counter = this.index;
			this.index++;
			final var element = this.input.charAt(counter);
			return Optional.of(element);
		} else {
			return Optional.empty();
		}
	}

	public Stream<String> stream() {
		return this.segments.stream();
	}
}
