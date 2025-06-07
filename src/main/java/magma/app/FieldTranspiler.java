package magma.app;

import java.util.Arrays;

class FieldTranspiler {
    static String transpileFields(String source) {
        var lines = source.split("\\R");
        var out = new StringBuilder();
        for (var line : lines) {
            var trimmed = line.trim();
            if (!trimmed.endsWith(";") || trimmed.contains("(") || trimmed.startsWith("import") ||
                trimmed.startsWith("return") || trimmed.startsWith("let ")) {
                out.append(line).append(System.lineSeparator());
                continue;
            }

            var indent = line.substring(0, line.indexOf(trimmed));
            var withoutSemi = trimmed.substring(0, trimmed.length() - 1).trim();
            var eq = withoutSemi.indexOf('=');
            if (eq != -1) {
                withoutSemi = withoutSemi.substring(0, eq).trim();
            }
            var tokens = withoutSemi.split("\\s+");
            if (tokens.length < 2) {
                out.append(line).append(System.lineSeparator());
                continue;
            }

            var name = tokens[tokens.length - 1];
            var type = tokens[tokens.length - 2];
            var modArray = Arrays.copyOf(tokens, tokens.length - 2);
            var modifiers = replaceFinalWithReadonly(modArray);
            var tsType = TypeMapper.toTsType(type);
            out.append(indent);
            if (!modifiers.isBlank()) {
                out.append(modifiers).append(" ");
            }
            out.append(name).append(": ").append(tsType).append(";")
               .append(System.lineSeparator());
        }
        return out.toString().trim();
    }

    private static String replaceFinalWithReadonly(String[] mods) {
        for (var i = 0; i < mods.length; i++) {
            if (mods[i].equals("final")) {
                mods[i] = "readonly";
            }
        }
        return String.join(" ", mods).trim();
    }
}
