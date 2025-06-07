package com.example.app;

class TypeMapper {
    static String toTsParams(String params) {
        if (params.isBlank()) {
            return "";
        }
        java.util.List<String> out = new java.util.ArrayList<>();
        for (String p : params.split(",")) {
            String[] parts = p.trim().split("\\s+");
            if (parts.length == 0) continue;
            String name = parts[parts.length - 1];
            String type = parts.length > 1 ? parts[parts.length - 2] : "any";
            out.add(name + ": " + toTsType(type));
        }
        return String.join(", ", out);
    }

    static String toTsType(String javaType) {
        int genericStart = javaType.indexOf('<');
        int genericEnd = javaType.lastIndexOf('>');
        if (genericStart != -1 && genericEnd != -1 && genericEnd > genericStart) {
            return mapGeneric(javaType, genericStart, genericEnd);
        }
        if (javaType.endsWith("[]")) {
            String element = javaType.substring(0, javaType.length() - 2);
            return toTsType(element) + "[]";
        }
        return switch (javaType) {
            case "int", "long", "float", "double" -> "number";
            case "boolean", "Boolean" -> "boolean";
            case "char", "Character", "String" -> "string";
            case "void" -> "void";
            default -> "any";
        };
    }

    private static String mapGeneric(String javaType, int start, int end) {
        String base = javaType.substring(0, start).trim();
        String params = javaType.substring(start + 1, end);
        java.util.List<String> mapped = new java.util.ArrayList<>();
        for (String p : params.split(",")) {
            mapped.add(toTsType(p.trim()));
        }
        return base + "<" + String.join(", ", mapped) + ">";
    }
}
