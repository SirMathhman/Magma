package magma.app;

import java.util.ArrayList;
import java.util.List;

class TypeMapper {
    static String toTsParams(String params) {
        if (params.isBlank()) {
            return "";
        }
        List<String> out = new ArrayList<>();
        for (var p : params.split(",")) {
            var parts = p.trim().split("\\s+");
            if (parts.length == 0) continue;
            var name = parts[parts.length - 1];
            var type = parts.length > 1 ? parts[parts.length - 2] : "any";
            out.add(name + ": " + toTsType(type));
        }
        return String.join(", ", out);
    }

    static String toTsType(String javaType) {
        var genericStart = javaType.indexOf('<');
        var genericEnd = javaType.lastIndexOf('>');
        if (genericStart != -1 && genericEnd != -1 && genericEnd > genericStart) {
            return mapGeneric(javaType, genericStart, genericEnd);
        }
        if (javaType.endsWith("[]")) {
            var element = javaType.substring(0, javaType.length() - 2);
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
        var base = javaType.substring(0, start).trim();
        var params = javaType.substring(start + 1, end);
        List<String> mapped = new ArrayList<>();
        for (var p : params.split(",")) {
            mapped.add(toTsType(p.trim()));
        }
        return base + "<" + String.join(", ", mapped) + ">";
    }
}
