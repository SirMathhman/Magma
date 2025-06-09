/*public */struct Main {
};
/*public static void main(String[] args) {
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
        final var segments = new ArrayList<String>();
        var buffer = new StringBuilder();
        var depth = 0;
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            buffer.append(c);
            if (c == ';' && depth == 0) {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
            }
            else {
                if (c == '{') {
                    depth++;
                }
                if (c == '}') {
                    depth--;
                }
            }
        }
        segments.add(buffer.toString());
        return segments;
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
        if(classIndex >= 0) {
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