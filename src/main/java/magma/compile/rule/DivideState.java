package magma.compile.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DivideState {
	public final List<String> segments;
	private StringBuilder buffer;
	private int depth;

	public DivideState(StringBuilder buffer, int depth, List<String> segments) {
		this.buffer = buffer; this.depth = depth; this.segments = segments;
	}

	public DivideState() {
		this(new StringBuilder(), 0, new ArrayList<>());
	}

	Stream<String> stream() {
		return segments.stream();
	}

	DivideState enter() {
		this.depth = depth + 1; return this;
	}

	DivideState advance() {
		segments.add(buffer.toString()); this.buffer = new StringBuilder(); return this;
	}

	DivideState append(char c) {
		buffer.append(c); return this;
	}

	DivideState exit() {
		this.depth = depth - 1; return this;
	}

	boolean isShallow() {
		return depth == 1;
	}

	boolean isLevel() {
		return depth == 0;
	}
}
