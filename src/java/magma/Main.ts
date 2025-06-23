/*public class Main {
    private interface DivideState {
        DivideState append(char c);*//*DivideState advance();*//*Collection<String> unwrap();*//*}

    private static class MutableDivideState implements DivideState {
        private final Collection<String> segments;*//*private StringBuilder buffer;*//*private MutableDivideState(final Collection<String> segments, final StringBuilder buffer) {
            this.segments = new ArrayList<>(segments);*//*this.buffer = buffer;*//*}

        private MutableDivideState() {
            this(new ArrayList<>(), new StringBuilder());*//*}

        @Override
        public Collection<String> unwrap() {
            return Collections.unmodifiableCollection(segments);*//*}

        @Override
        public DivideState append(final char c) {
            buffer.append(c);*//*return this;*//*}

        @Override
        public DivideState advance() {
            segments.add(buffer.toString());*//*buffer = new StringBuilder();*//*return this;*//*}
    }

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
        final DivideState state = new MutableDivideState();*//*final var length = input.length();*//*var current = state;*//*for (var i = 0;*//*i < length;*//*i++) {
            final var c = input.charAt(i);*//*current = Main.fold(current, c);*//*}

        return current.advance()
                .unwrap();*//*}

    private static DivideState fold(final DivideState state, final char c) {
        final var appended = state.append(c);*//*if (';*//*' == c)
            return appended.advance();*//*return appended;*//*}

    private static String generatePlaceholder(final String input) {
        final var replaced = input.replace("start", "start")
                .replace("end", "end");*//*return "start" + replaced + "end";*//*}
}*/