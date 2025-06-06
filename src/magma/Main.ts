export class Main {
	class Tuple {/**/}
	/*public static*/ main(/*String[] args*/): void/* {
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var target = source.resolveSibling("Main.ts");

            final var input = Files.readString(source);
            final var output = compile(input);
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }*/
	/*private static*/ compile(/*String input*/): String/* {
        return compileStatements(input, Main::compileRootSegment);
    }*/
	/*private static*/ compileStatements(/*String input, Function<String, String> mapper*/): String/* {
        final var segments = new ArrayList<String>();
        final var length = input.length();
        var buffer = new StringBuilder();
        var depth = 0;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            buffer.append(c);
            if (c == ';' && depth == 0) {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
            }
            else if (c == '}' && depth == 1) {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
                depth--;
            }
            else {
                if (c == '{') {
                    depth++;
                }
                if (c == '}') {
                    depth--;
                }
            }
        }*/
	/*segments.add*/(/*buffer.toString(*/): /*);*/
	/*final var output =*/ StringBuilder(/**/): new/*;*/
	/*for*/(/*var segment : segments*/): /* {
            output.append(mapper.apply(segment));
        }*/
	/*return output.toString*/(/**/): /*;*//*
    */}/*

    private static String compileRootSegment(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return compileStructure(input, "class ", 0).orElseGet(() -> generatePlaceholder(input));
    }*//*

    private static Optional<String> compileStructure(String input, String keyword, int depth) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex >= 0) {
            final var modifiersString = input.substring(0, classIndex);
            final var afterClass = input.substring(classIndex + keyword.length());
            final var contentStart = afterClass.indexOf("{");
            if (contentStart >= 0) {
                final var beforeContent = afterClass.substring(0, contentStart).strip();
                final var withEnd = afterClass.substring(contentStart + "{".length()).strip();
                if (withEnd.endsWith("}")) {
                    final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());
                    final var outputContent = compileStatements(inputContent, Main::compileClassSegment);
                    final var modifiers = modifiersString.contains("public") ? "export " : "";

                    if (beforeContent.endsWith(")")) {
                        final var withoutParamEnd = beforeContent.substring(0, beforeContent.length() - ")".length());
                        final var paramStart = withoutParamEnd.indexOf("(");
                        if (paramStart >= 0) {
                            final var name = withoutParamEnd.substring(0, paramStart).strip();
                            return generateClass(depth, modifiers, name, outputContent);
                        }
                    }

                    return generateClass(depth, modifiers, beforeContent, outputContent);
                }
            }
        }
        return Optional.empty();
    }*/class " + beforeContent + " {/*" + outputContent + "}");
    */}/*

    private static String compileClassSegment(String input) {
        final var maybeRecord = compileStructure(input, "record ", 1);
        if (maybeRecord.isPresent()) {
            return maybeRecord.get();
        }

        final var paramStart = input.indexOf("(");
        if (paramStart >= 0) {
            final var inputDefinition = input.substring(0, paramStart);
            final var withParams = input.substring(paramStart + "(".length());
            final var paramEnd = withParams.indexOf(")");
            if (paramEnd >= 0) {
                final var params = withParams.substring(0, paramEnd);
                final var withBraces = withParams.substring(paramEnd + ")".length());
                final var outputDefinition = compileDefinition(inputDefinition);
                return "\n\t" + outputDefinition.left + "(" + generatePlaceholder(params) + "): " + outputDefinition.right + generatePlaceholder(withBraces);
            }
        }

        return generatePlaceholder(input);
    }*//*

    private static Tuple compileDefinition(String input) {
        final var stripped = input.strip();
        final var nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator >= 0) {
            final var beforeName = stripped.substring(0, nameSeparator).strip();
            final var name = stripped.substring(nameSeparator + " ".length());
            final var typeSeparator = beforeName.lastIndexOf(" ");
            if (typeSeparator >= 0) {
                final var beforeType = beforeName.substring(0, typeSeparator);
                final var type = beforeName.substring(typeSeparator + " ".length());
                return new Tuple(generatePlaceholder(beforeType) + " " + name, type);
            }
        }

        return new Tuple(generatePlaceholder(stripped), "");
    }*//*

    private static String generatePlaceholder(String input) {
        final var replaced = input
                .replace("start", "start")
                .replace("end", "end");

        return "start" + replaced + "end";
    }*//*
}*/