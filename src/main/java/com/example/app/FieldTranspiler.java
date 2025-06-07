package com.example.app;

class FieldTranspiler {
    static String transpileFields(String source) {
        String[] lines = source.split("\\R");
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.endsWith(";") || trimmed.contains("(") || trimmed.startsWith("import") ||
                trimmed.startsWith("return") || trimmed.startsWith("let ")) {
                out.append(line).append(System.lineSeparator());
                continue;
            }

            String indent = line.substring(0, line.indexOf(trimmed));
            String withoutSemi = trimmed.substring(0, trimmed.length() - 1).trim();
            int eq = withoutSemi.indexOf('=');
            if (eq != -1) {
                withoutSemi = withoutSemi.substring(0, eq).trim();
            }
            String[] tokens = withoutSemi.split("\\s+");
            if (tokens.length < 2) {
                out.append(line).append(System.lineSeparator());
                continue;
            }

            String name = tokens[tokens.length - 1];
            String type = tokens[tokens.length - 2];
            String[] modArray = java.util.Arrays.copyOf(tokens, tokens.length - 2);
            String modifiers = replaceFinalWithReadonly(modArray);
            String tsType = TypeMapper.toTsType(type);
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
        for (int i = 0; i < mods.length; i++) {
            if (mods[i].equals("final")) {
                mods[i] = "readonly";
            }
        }
        return String.join(" ", mods).trim();
    }
}
