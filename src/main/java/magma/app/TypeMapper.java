package magma.app;

import magma.list.JdkList;
import magma.list.ListLike;

class TypeMapper {
    static String toTsParams(String params) {
        if (params.isBlank()) {
            return "";
        }
        ListLike<String> out = JdkList.create();
        for (var p : params.split(",")) {
            var parts = p.trim().split("\\s+");
            if (parts.length == 0) continue;
            var name = parts[parts.length - 1];
            var type = parts.length > 1 ? parts[parts.length - 2] : "any";
            out.add(name + ": " + toTsType(type));
        }
        var result = new StringBuilder();
        for (var i = 0; i < out.size(); i++) {
            if (i > 0) result.append(", ");
            result.append(out.get(i));
        }
        return result.toString();
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
            default -> javaType;
        };
    }

    private static String mapGeneric(String javaType, int start, int end) {
        var base = javaType.substring(0, start).trim();
        var params = javaType.substring(start + 1, end);
        ListLike<String> mapped = JdkList.create();
        for (var p : params.split(",")) {
            mapped.add(toTsType(p.trim()));
        }
        var joined = new StringBuilder();
        for (var i = 0; i < mapped.size(); i++) {
            if (i > 0) joined.append(", ");
            joined.append(mapped.get(i));
        }
        return base + "<" + joined + ">";
    }
}
