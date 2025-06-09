/*public */struct Main {
};
/*private static */struct State {
};
/*private final List<String> segments;*//*
        private StringBuilder buffer;*//*
        private int depth;*//*

        private State(List<String> segments, StringBuilder buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public State() {
            this(new ArrayList<>(), new StringBuilder(), 0);
        }

        private boolean isLevel() {
            return depth == 0;
        }

        private State append(char c) {
            buffer.append(c);
            return this;
        }

        private State advance() {
            segments.add(buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }

        private State enter() {
            this.depth = depth + 1;
            return this;
        }

        private State exit() {
            this.depth = depth - 1;
            return this;
        }
    }

    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var input = Files.readString(source);
            final var target = source.resolveSibling("Main.c");
            final var string = compile(input);
            Files.writeString(target, string);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        return compileStatements(input, Main::compileRootSegment);*//*
    }

    private static String compileStatements(String input, Function<String, String> mapper) {
        final var segments = divide(input);*//*
        final var output = new StringBuilder();*//*
        for (var segment : segments) {
            output.append(mapper.apply(segment));
        }

        return output.toString();*//*
    }

    private static List<String> divide(String input) {
        var current = new State();*//*
        for (var i = 0;*//* i < input.length();*//* i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments;*//*
    }

    private static State fold(State state, char c) {
        final var appended = state.append(c);*//*
        if (c == ';*//*' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '{') {
            return appended.enter();
        }
        if (c == '}') {
            return appended.exit();
        }
        return appended;*//*
    }

    private static String compileRootSegment(String input) {
        final var stripped = input.strip();*//*
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return compileClass(stripped).orElseGet(() -> generatePlaceholder(input));*//*
    }

    private static Optional<String> compileClass(String input) {
        final var contentStart = input.indexOf('{');
        if (contentStart >= 0) {
            final var beforeContent = input.substring(0, contentStart);
            final var withEnd = input.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                final var content = withEnd.substring(0, withEnd.length() - "}".length());
                final var header = compileClassDefinition(beforeContent);
                return Optional.of(header + "{\n};\n" + compileStatements(content, Main::compileClassSegment));
            }
        }

        return Optional.empty();*//*
    }

    private static String compileClassSegment(String input) {
        return compileClass(input).orElseGet(() -> generatePlaceholder(input));*//*
    }

    private static String compileClassDefinition(String input) {
        final var classIndex = input.indexOf("class ");*//*
        if (classIndex >= 0) {
            final var beforeKeyword = input.substring(0, classIndex);
            final var afterKeyword = input.substring(classIndex + "class ".length());
            return generatePlaceholder(beforeKeyword) + "struct " + afterKeyword;
        }

        return generatePlaceholder(input);*//*
    }

    private static String generatePlaceholder(String input) {
        return "start" + input
                .replace("start", "start")
                .replace("end", "end") + "end";*//*
    */