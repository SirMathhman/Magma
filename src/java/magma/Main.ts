/*public class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");*//*final var input = Files.readString(source);*//*final var output = Main.compile(input);*//*final var target = source.resolveSibling("Main.ts");*//*Files.writeString(target, output);*//*} catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();*//*}
    }

    private static String compile(final CharSequence input) {
        final var segments = Main.divide(input);*//*final var output = new StringBuilder();*//*for (final var segment : segments)
            output.append(Main.compileRootSegment(segment));*//*return output.toString();*//*}

    private static String compileRootSegment(final String input) {
        final var stripped = input.strip();*//*if (stripped.startsWith("package ") || stripped.startsWith("import "))
            return "";*//*return Main.generatePlaceholder(stripped);*//*}

    private static Collection<String> divide(final CharSequence input) {
        final Collection<String> segments = new ArrayList<>();*//*var buffer = new StringBuilder();*//*final var length = input.length();*//*for (var i = 0;*//*i < length;*//*i++) {
            final var c = input.charAt(i);*//*buffer.append(c);*//*if (';*//*' == c) {
                segments.add(buffer.toString());*//*buffer = new StringBuilder();*//*}
        }
        segments.add(buffer.toString());*//*return segments;*//*}

    private static String generatePlaceholder(final String input) {
        final var replaced = input.replace("start", "start")
                .replace("end", "end");*//*return "start" + replaced + "end";*//*}
}*/