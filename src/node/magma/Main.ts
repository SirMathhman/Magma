/*public */class Main {
	/*private static */class State {
		/*private final*/segments : /*Collection<String>*/ = /* new ArrayList<>()*/;
		/*private*/buffer : /*StringBuilder*/ = /* new StringBuilder()*/;
		/*private*/depth : /*int*/ = /* 0*/;
		/*private boolean isLevel() {
			return depth == 0;
		}*/
		/*private boolean isShallow() {
			return depth == 1;
		}*/
		/*private Stream<String> stream() {
			return segments.stream();
		}*/
		/*private State exit() {
			this.depth = depth - 1;
			return this;
		}*/
		/*private State enter() {
			this.depth = depth + 1;
			return this;
		}*/
		/*private State advance() {
			segments.add(buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}*/
		/*private State append(char c) {
			buffer.append(c);
			return this;
		}*/
		/**/}
	/*public static void main(String[] args) {
		try {
			final String input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "src", "node", "magma", "Main.ts"), compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}*/
	/*private static String compile(String input) {
		return compileStatements(input, Main::compileRootSegment);
	}*/
	/*private static String compileStatements(String input, Function<String, String> mapper) {
		return divide(input).map(mapper).collect(Collectors.joining());
	}*/
	/*private static String compileRootSegment(String input) {
		final String strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return compileClass(strip, 0).orElseGet(() -> wrap(strip));
	}*/
	/*private static Optional<String> compileClass(String input, int depth) {
		final int i = input.indexOf("*/class ");
		if (i < 0) return Optional.empty();
		final String modifiers = input.substring(0, i);
		final String afterKeyword = input.substring(i + "class ".length());

		final int i1 = afterKeyword.indexOf(" {
		/*");*/
		/*if (i1 < 0) return Optional.empty();*/
		/*final*/name : /*String*/ = /* afterKeyword.substring(0, i1).strip()*/;
		/*final String substring = afterKeyword.substring(i1 + "{".length()).strip();

		if (!substring.endsWith("}*/
		/*")) return Optional.empty();*/
		/*final*/content : /*String*/ = /* substring.substring(0, substring.length() - 1)*/;
		/*return Optional.of(wrap(modifiers) + "*/class " + name + " {
			/*" +
											 compileStatements(content, input1 -> compileClassSegment(input1, depth + 1)) + "*/}
		/*");*/
		/*}

	private static String compileClassSegment(String input, int depth) {
		final*/strip : /*String*/ = /* input.strip()*/;
		/*return System.lineSeparator() + "\t".repeat(depth) + compileClassSegmentValue(depth, strip);*/
		/*}

	private static String compileClassSegmentValue(int depth, String input) {
		return compileField(input).orElseGet(() -> compileClass(input, depth).orElseGet(() -> wrap(input)));*/
		/*}

	private static Optional<String> compileField(String input) {
		if (!input.endsWith(";*/
		/*")) return Optional.empty();*/
		/*final*/substring : /*String*/ = /* input.substring(0, input.length() - "*/;
		/*".length());*/
		/*final*/i : /*int*/ = /* substring.indexOf("=")*/;
		/*if (i < 0) return Optional.empty();*/
		/*final*/substring1 : /*String*/ = /* substring.substring(0, i)*/;
		/*final*/substring2 : /*String*/ = /* substring.substring(i + "=".length())*/;
		/*return Optional.of(compileDefinition(substring1)*/" : /*+*/ = /* " + wrap(substring2) + "*/;
		/*");*/
		/*}

	private static String compileDefinition(String input) {
		final*/strip : /*String*/ = /* input.strip()*/;
		/*final*/i : /*int*/ = /* strip.lastIndexOf(" ")*/;
		/*if (i >= 0) {
			final String beforeName = strip.substring(0, i);
			final String name = strip.substring(i + " ".length());
			final int i1 = beforeName.lastIndexOf(" ");
			if (i1 >= 0) {
				final String beforeType = beforeName.substring(0, i1);
				final String type = beforeName.substring(i1 + " ".length());
				return wrap(beforeType) + name + " : " + wrap(type);
			}
		}*/
		/*return wrap(strip);*/
		/*}

	private static Stream<String> divide(String input)*/current : /*{
		State*/ = /* new State()*/;
		/*for*/i : /*(int*/ = /* 0*/;
		/*i < input.length();*/
		/*i++) {
			final char c = input.charAt(i);
			current = fold(current, c);
		}*/
		/*return current.advance().stream();*/
		/*}

	private static State fold(State state, char c) {
		final*/appended : /*State*/ = /* state.append(c)*/;
		/*if (c*/ = /*= '*/;
		/*' && appended.isLevel()) return appended.advance();*/
		/*if (c*/ = /*= '}' && appended.isShallow()) return appended.advance().exit();
		if (c == '{') return appended.enter()*/;
		/*if (c*/ = /*= '}') return appended.exit();
		return appended*/;}
	/*private static String wrap(String input) {
		final String replaced = input.replace("start", "start").replace("end", "end");
		return "start" + replaced + "end";
	}*/
	/**/}/**/