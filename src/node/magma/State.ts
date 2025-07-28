/*import java.util.ArrayList;*/
/*import java.util.Collection;*/
/*import java.util.stream.Stream;*/
/*public final */class /*State {
	private final Collection<String> segments = new ArrayList<>();
	private final StringBuilder buffer = new StringBuilder();
	private int depth = 0;

	Stream<String> stream() {
		return this.segments.stream();
	}

	State advance() {
		this.segments.add(this.buffer.toString());
		this.buffer.setLength(0);
		return this;
	}

	State append(final char c) {
		this.buffer.append(c);
		return this;
	}

	boolean isLevel() {
		return 0 == this.depth;
	}

	State enter() {
		this.depth++;
		return this;
	}

	State exit() {
		this.depth--;
		return this;
	}
}*/
