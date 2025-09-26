/*public class Main {
	private static class State {
		private final Collection<String> segments;
		private StringBuilder buffer;
		private int depth;

		private State(StringBuilder buffer, int depth, Collection<String> segments) {
			this.buffer = buffer;
			this.depth = depth;
			this.segments = segments;
		}

		private Stream<String> stream() {
			return segments.stream();
		}

		private State exit() {
			setDepth(getDepth() - 1);
			return this;
		}

		private State enter() {
			setDepth(getDepth() + 1);
			return this;
		}

		private State advance() {
			segments.add(getBuffer().toString());
			setBuffer(new StringBuilder());
			return this;
		}

		private State append(char c) {
			getBuffer().append(c);
			return this;
		}

		public StringBuilder getBuffer() {
			return buffer;
		}

		public void setBuffer(StringBuilder buffer) {
			this.buffer = buffer;
		}

		public int getDepth() {
			return depth;
		}

		public void setDepth(int depth) {
			this.depth = depth;
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
		return divide(input).map(String::strip)
												.filter(segment -> !segment.startsWith("package ") && !segment.startsWith("import "))
												.map(Main::wrap)
												.collect(Collectors.joining());
	}

	private static Stream<String> divide(String input) {
		final ArrayList<String> segments = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		int depth = 0;
		return getStringStream(input, new State(buffer, depth, segments));
	}

	private static Stream<String> getStringStream(String input, State state) {
		State current = state;
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			current = extracted(current, c);
		}

		return current.advance().stream();
	}

	private static State extracted(State state, char c) {
		final State appended = state.append(c);
		if (c == ';' && appended.getDepth() == 0) return appended.advance();
		if (c == '{') return appended.enter();
		if (c == '}') return appended.exit();
		return appended;
	}

	private static String wrap(String input) {
		final String replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}
}*/