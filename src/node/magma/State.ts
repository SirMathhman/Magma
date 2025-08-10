/*import java.util.ArrayList;*/
/*import java.util.Collection;*/
/*import java.util.stream.Stream;*/
/*public */class State {/*public final Collection<String> segments;
	private int depth = 0;
	private StringBuilder buffer;

	public State(Collection<String> segments, StringBuilder buffer) {
		this.segments = segments;
		this.buffer = buffer;
	}

	public State() {
		this(new ArrayList<>(), new StringBuilder());
	}

	Stream<String> stream() {
		return segments.stream();
	}

	State advance() {
		segments.add(buffer.toString());
		this.buffer = new StringBuilder();
		return this;
	}

	State append(char c) {
		buffer.append(c);
		return this;
	}

	public boolean isLevel() {
		return depth == 0;
	}

	public State enter() {
		depth++;
		return this;
	}

	public State exit() {
		depth--;
		return this;
	}
*/}
