/*public */struct Main {
};
/*private static class State {
        private final List<String> segments;
        private StringBuilder buffer;
        private int depth;

        private State(List<String> segments, StringBuilder buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public State() {
            this(new ArrayList<>(), new StringBuilder(), 0);
        }

        private boolean isLevel() {
            return getDepth() == 0;
        }

        private State append(char c) {
            getBuffer().append(c);
            return this;
        }

        private State advance() {
            segments().add(getBuffer().toString());
            setBuffer(new StringBuilder());
            return this;
        }

        private State enter() {
            setDepth(getDepth() + 1);
            return this;
        }

        private State exit() {
            setDepth(getDepth() - 1);
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

        public List<String> segments() {
            return segments;
        }
    }

    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var input = Files.readString(source);
            final var target = source.resolveSibling("Main.c");
            final var segments = divide(input);
            final var output = new StringBuilder();
            for (var segment : segments) {
                output.append(compileRootSegment(segment));
            }

            Files.writeString(target, output.toString());
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static List<String> divide(String input) {
        var current = new State();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments;
    }

    private static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '{') {
            return appended.enter();
        }
        if (c == '}') {
            return appended.exit();
        }
        return appended;
    }

    private static String compileRootSegment(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        final var contentStart = stripped.indexOf('{');
        if (contentStart >= 0) {
            final var beforeContent = stripped.substring(0, contentStart);
            final var withEnd = stripped.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                final var content = withEnd.substring(0, withEnd.length() - "}".length());
                final var header = compileClassDefinition(beforeContent);
                return header + "{\n};\n" + generatePlaceholder(content);
            }
        }

        return generatePlaceholder(input);
    }

    private static String compileClassDefinition(String input) {
        final var classIndex = input.indexOf("class ");
        if (classIndex >= 0) {
            final var beforeKeyword = input.substring(0, classIndex);
            final var afterKeyword = input.substring(classIndex + "class ".length());
            return generatePlaceholder(beforeKeyword) + "struct " + afterKeyword;
        }

        return generatePlaceholder(input);
    }

    private static String generatePlaceholder(String input) {
        return "start" + input
                .replace("start", "start")
                .replace("end", "end") + "end";
    }
*/