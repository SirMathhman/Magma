package magma;

import java.util.ArrayList;
import java.util.Optional;

public class State {
	public final String input;
	public final ArrayList<String> segments;
	private StringBuilder buffer;
	private int depth;
	private int index = 0;

	public State(String input, StringBuilder buffer, int depth, ArrayList<String> segments) {
		this.input = input;
		this.buffer = buffer;
		this.depth = depth;
		this.segments = segments;
	}

	public String getInput() {
		return this.input;
	}

	public StringBuilder getBuffer() {
		return this.buffer;
	}

	public void setBuffer(StringBuilder buffer) {
		this.buffer = buffer;
	}

	public int getDepth() {
		return this.depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String input() {
		return this.input;
	}

	public ArrayList<String> segments() {
		return this.segments;
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
}
