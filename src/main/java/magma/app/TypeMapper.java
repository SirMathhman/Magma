package magma.app;

import magma.list.JdkList;
import magma.list.ListLike;

class TypeMapper {
    static String toTsParams(String params) {
        if (params.isBlank()) {
            return "";
        }
        ListLike<String> pieces = split(params);
        ListLike<String> out = JdkList.create();
        for (var i = 0; i < pieces.size(); i++) {
            var p = pieces.get(i);
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
            var base = javaType.substring(0, genericStart).trim();
            var name = base.substring(base.lastIndexOf('.') + 1);
            var params = javaType.substring(genericStart + 1, genericEnd);
            switch (name) {
                case "Function" -> {
                    return mapFunction(params);
                }
                case "BiFunction" -> {
                    return mapBiFunction(params);
                }
                case "Supplier" -> {
                    var ts = mapParams(params, 0);
                    return "() => " + ts.returnType;
                }
                case "Consumer" -> {
                    var ts = mapParams(params, 1);
                    return "(" + ts.params + ") => void";
                }
                case "BiConsumer" -> {
                    var ts = mapParams(params, 2);
                    return "(" + ts.params + ") => void";
                }
                case "Predicate" -> {
                    var ts = mapParams(params, 1);
                    return "(" + ts.params + ") => boolean";
                }
                case "UnaryOperator" -> {
                    var ts = mapParams(params, 1);
                    return "(" + ts.params + ") => " + ts.returnType;
                }
                case "BinaryOperator" -> {
                    var ts = mapParams(params, 2);
                    return "(" + ts.params + ") => " + ts.returnType;
                }
                default -> {
                    return mapGeneric(javaType, genericStart, genericEnd);
                }
            }
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
        var pieces = split(params);
        for (var i = 0; i < pieces.size(); i++) {
            mapped.add(toTsType(pieces.get(i).trim()));
        }
        var joined = new StringBuilder();
        for (var i = 0; i < mapped.size(); i++) {
            if (i > 0) joined.append(", ");
            joined.append(mapped.get(i));
        }
        return base + "<" + joined + ">";
    }

    private static ListLike<String> split(String text) {
        ListLike<String> out = JdkList.create();
        var depth = 0;
        var part = new StringBuilder();
        for (var i = 0; i < text.length(); i++) {
            var c = text.charAt(i);
            if (c == '<') depth++; else if (c == '>') depth--;
            if (c == ',' && depth == 0) {
                out.add(part.toString());
                part.setLength(0);
                continue;
            }
            part.append(c);
        }
        out.add(part.toString());
        return out;
    }

    private static record Params(String params, String returnType) {}

    private static Params mapParams(String params, int paramCount) {
        ListLike<String> out = JdkList.create();
        var parts = split(params);
        for (var i = 0; i < Math.min(paramCount, parts.size()); i++) {
            out.add("arg" + i + ": " + toTsType(parts.get(i).trim()));
        }
        var ret = parts.size() > paramCount ? toTsType(parts.get(parts.size() - 1).trim()) : "void";
        var joined = new StringBuilder();
        for (var i = 0; i < out.size(); i++) {
            if (i > 0) joined.append(", ");
            joined.append(out.get(i));
        }
        return new Params(joined.toString(), ret);
    }

    private static String mapFunction(String params) {
        var ts = mapParams(params, 1);
        return "(" + ts.params + ") => " + ts.returnType;
    }

    private static String mapBiFunction(String params) {
        var ts = mapParams(params, 2);
        return "(" + ts.params + ") => " + ts.returnType;
    }
}
