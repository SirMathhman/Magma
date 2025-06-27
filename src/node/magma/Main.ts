/*package magma;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Paths;*/
/*import java.util.List;*/
/*import java.util.function.Function;*/
/*public */class Main {
	/*public static final String*/ LINE_SEPARATOR = /* System.lineSeparator()*/
	/*private Main() {
    }*/
	/*public static void main(final String[] args) {
        final var root = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(root)) {
            final var sources = stream.filter(path -> path.toString().endsWith(".java")).toList();

            for (final var source : sources) {
                final var relative = root.relativize(source.getParent());
                final var input = Files.readString(source);
                final var output = Main.compileRoot(input);
                final var targetParent = Paths.get(".", "src", "node").resolve(relative);
                if (!Files.exists(targetParent))
                    Files.createDirectories(targetParent);

                final var fileName = source.getFileName().toString();
                final var separator = fileName.lastIndexOf('.');
                final var name = fileName.substring(0, separator);
                final var target = targetParent.resolve(name + ".ts");
                Files.writeString(target, output);
            }
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }*/
	/*private static String compileRoot(final String input) {
        return Main.compileStatements(input, Main::compileRootSegment);
    }*/
	/*private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
        final var segments = Main.divide(input);
        final var output = new StringBuilder();
        for (final var segment : segments)
            output.append(mapper.apply(segment));
        return output.toString();
    }*/
	/*private static String compileRootSegment(final String input) {
        return Main.compileRootSegmentValue(input.strip()) + Main.LINE_SEPARATOR;
    }*/
	/*private static String compileRootSegmentValue(final String input) {
        if (!input.isEmpty() && '}*/
	/*' == input.charAt(input.length() - 1)) {
            final var withoutEnd = input.substring(0, input.length() - "}*/
	/*".length());*/
	/*final var contentStart = withoutEnd.indexOf('{');
            if (0 <= contentStart) {
                final var beforeContent = withoutEnd.substring(0, contentStart);
                final var content = withoutEnd.substring(contentStart + "{".length());
                return Main.compileStructureHeader(beforeContent) + " {" +
                       Main.compileStatements(content, Main::compileStructureSegment) + "}";
            }
        }

        return Main.generatePlaceholder(input);
    }*/
	/*private static String compileStructureSegment(final String input) {
        final var strip = input.strip();
        return Main.LINE_SEPARATOR + "\t" + Main.compileStructureSegmentValue(strip);
    }*/
	/*private static String compileStructureSegmentValue(final String input) {
        if (!input.isEmpty() && ';' == input.charAt(input.length() - 1)) {
            final var withoutEnd = input.substring(0, input.length() - ";".length());
            final var separator = withoutEnd.indexOf('=');
            if (0 <= separator) {
                final var before = withoutEnd.substring(0, separator);
                final var after = withoutEnd.substring(separator + "=".length());
                return Main.compileDefinition(before) + " = " + Main.generatePlaceholder(after);
            }
        }

        return Main.generatePlaceholder(input);
    }*/
	/*private static String compileDefinition(final String input) {
        final var strip = input.strip();
        final var separator = strip.lastIndexOf(' ');
        if (0 <= separator) {
            final var before = strip.substring(0, separator);
            final var after = strip.substring(separator + " ".length());
            return Main.generatePlaceholder(before) + " " + after;
        }

        return Main.generatePlaceholder(strip);
    }*/
	/*private static String compileStructureHeader(final String input) {
        final var classIndex = input.indexOf("class ");
        if (0 <= classIndex) {
            final var beforeKeyword = input.substring(0, classIndex);
            final var afterKeyword = input.substring(classIndex + "class ".length()).strip();
            final var implementsIndex = afterKeyword.indexOf("implements ");
            if (0 <= implementsIndex) {
                final var beforeImplements = afterKeyword.substring(0, implementsIndex);
                final var afterImplements = afterKeyword.substring(implementsIndex + "implements ".length());
                return Main.generatePlaceholder(beforeKeyword) + "class " + beforeImplements +
                       Main.generatePlaceholder("implements " + afterImplements);
            } else
                return Main.generatePlaceholder(beforeKeyword) + "class " + afterKeyword;
        }

        return Main.generatePlaceholder(input);
    }*/
	/*private static List<String> divide(final CharSequence input) {
        State current = new MutableState(input);
        while (true) {
            final var maybe = current.pop();
            if (maybe.isEmpty())
                break;

            final var tuple = maybe.get();
            current = tuple.left();
            current = Main.fold(current, tuple.right());
        }

        return current.advance().unwrap();
    }*/
	/*private static State fold(final State state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();
        if ('}*/
	/*'*/ = /*= c && appended.isShallow())
            return appended.exit().advance()*/
	/*if ('{' == c)
            return appended.enter();
        if ('}*/
	/*'*/ = /*= c)
            return appended.exit()*/
	/*return appended;*/
	/**/}
/*private static String generatePlaceholder(final String input) */ {
	/*return "stat" + input.replace("stat", "stat").replace("end", "end") + "end";*/
	/**/}
/*}*/
