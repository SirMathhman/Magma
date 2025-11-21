/*public class Main {
	public static void main(String[] args) {
		try {
			final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");*//*final var input = Files.readString(source);*//*final var target = source.resolveSibling("Main.c");*//*Files.writeString(target, compile(input));*//*} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();*//*}
	}

	private static String compile(String input) {
		final var segments = new ArrayList<String>();*//*var buffer = new StringBuilder();*//*for (var i = 0;*//*i < input.length();*//*i++) {
			final var c = input.charAt(i);*//*buffer.append(c);*//*if (c == ';*//*') {
				segments.add(buffer.toString());*//*buffer = new StringBuilder();*//*}
		}
		segments.add(buffer.toString());*//*return segments.stream().map(Main::compileRootSegment).collect(Collectors.joining());*//*}

	private static String compileRootSegment(String input) {
		final var stripped = input.strip();*//*if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";*//*}

		return wrap(stripped);*//*}

	private static String wrap(String input) {
		final var replaced = input.replace("start", "start").replace("end", "end");*//*return "start" + replaced + "end";*//*}
}*/