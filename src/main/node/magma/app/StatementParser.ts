import TypeMapper from "./TypeMapper";
import ExpressionParser from "./ExpressionParser";
import MethodStubber from "./MethodStubber";
import KNOWN_RETURNS from "./KNOWN_RETURNS";
export default class StatementParser {
    static void parseStatements(String[] lines, int start, int end, String indent,
                                StringBuilder stub, String returnType,
                                java.util.Map<String, String> returns,
                                java.util.Map<String, String> vars) {
        wrote: var;
        if (vars == null) vars = new java.util.HashMap<>();
        for(i++: end;): void {
            let body : unknown = lines[i].trim();
            // TODO
            // TODO
            if (body.contains("=>") && body.endsWith("{")) {
                // TODO
                // TODO
            }
            let next : unknown = handleControlBlock(body, lines, i, indent, stub, returns, returnType, vars);
            if (/* TODO */) {
                // TODO
                // TODO
            }
            if (body.startsWith("return")) {
                appendReturn(body, indent, stub, returnType);
                // TODO
            }
            // TODO
            // TODO
            // TODO
            // TODO
        }

    skipBody(lines: string[], index: number): number {
        let depth : number = 1;
        let i : unknown = /* TODO */;
        while (i < lines.length && depth > 0) {
            let body : unknown = /* TODO */;
            let + : depth = body.length().replace("{", "").length();
            let - : depth = body.length().replace("}", "").length();
            // TODO
        }
        return i;
    }

    private static int parseArrowBlock(String[] lines, int start, StringBuilder stub,
                                       java.util.Map<String, String> returns,
                                       java.util.Map<String, String> vars) {
        var indent = lines[start].substring(0, lines[start].indexOf(lines[start].trim()));
        stub.append(lines[start]).append(System.lineSeparator());
        var end = skipBody(lines, start);
        if(2: >): void {
            parseStatements(lines, /* TODO */, /* TODO */, indent, stub, /* TODO */, returns, vars);
        }
        stub.append(lines[end - 1]).append(System.lineSeparator());
        return end;
    }

    static void appendParsedBlock(StringBuilder stub, String indent, String keyword,
                                  String condition, String[] lines, int start, int end,
                                  java.util.Map<String, String> returns, String returnType,
                                  java.util.Map<String, String> vars) {
        stub.append(indent).append("    ").append(keyword);
        if(null: !=): void {
            stub.append("(/* TODO */).append(condition).append(/* TODO */));
        }
        stub.append(" {").append(System.lineSeparator());
        parseStatements(lines, start, end, indent + "    ", stub, returnType, returns, vars);
        stub.append(indent).append("    }").append(System.lineSeparator());
    }

    private static int handleControlBlock(String body, String[] lines, int index, String indent,
                                          StringBuilder stub, java.util.Map<String, String> returns,
                                          String returnType, java.util.Map<String, String> vars) {
        if(body.endsWith("{"): &&): void {
            let keyword : unknown = body.startsWith("else if");
            let cond : string = parseCondition(body);
            let blockEnd : number = skipBody(lines, index);
            appendParsedBlock(stub, indent, keyword, cond, lines, /* TODO */, /* TODO */, returns, returnType, vars);
            return blockEnd;
        }
        if(body.endsWith("{"): &&): void {
            let blockEnd : number = skipBody(lines, index);
            appendParsedBlock(stub, indent, "else", /* TODO */, lines, /* TODO */, /* TODO */, returns, returnType, vars);
            return blockEnd;
        }
        if(body.endsWith("{"): &&): void {
            let cond : string = parseCondition(body);
            let blockEnd : number = skipBody(lines, index);
            appendParsedBlock(stub, indent, "while", cond, lines, /* TODO */, /* TODO */, returns, returnType, vars);
            return blockEnd;
        }
        return index;
    }

    private static void appendParts(String[] parts, String indent, StringBuilder stub,
                                    java.util.Map<String, String> vars,
                                    java.util.Map<String, String> returns,
                                    String returnType) {
        for(parts: :): void {
            let trimmedPart : unknown = part.trim();
            // TODO
            if (trimmedPart.startsWith("return")) {
                appendReturn(trimmedPart, indent, stub, returnType);
                // TODO
            }
            if (trimmedPart.contains("=")) {
                stub.append(parseAssignment(trimmedPart, indent, vars, returns)).append(System.lineSeparator());
                // TODO
            }
            if (ExpressionParser.isInvokable(trimmedPart)) {
                stub.append(ExpressionParser.parseInvokable(trimmedPart, indent)).append(System.lineSeparator());
                // TODO
            }
            if (ExpressionParser.isMemberAccess(trimmedPart)) {
                stub.append(ExpressionParser.parseMemberAccess(trimmedPart, indent)).append(System.lineSeparator());
                // TODO
            }
            stub.append(indent).append("    // TODO").append(System.lineSeparator());
        }
    }

    appendReturn(stmt: string, indent: string, stub: StringBuilder, returnType: string): void {
        let expr : unknown = stmt.substring(6).trim();
        // TODO
        let expr : ")) = expr.substring(0, expr.length()).trim();
        let value : unknown = expr.isBlank().parseValue(expr, returnType);
        stub.append(indent);
        .append("    return");
        .append(value);
        // TODO
        // TODO
        .append(System.lineSeparator());
    }

    static String parseAssignment(String stmt, String indent,
                                  java.util.Map<String, String> vars,
                                  java.util.Map<String, String> returns) {
        var eq = stmt.indexOf('=');
        if(-1: ==): void {
            return /* TODO */;
        }
        var dest = stmt.substring(0, eq).trim();
        var rhs = stmt.substring(eq + 1).trim();
        var tokens = dest.split("\\s+");
        if(2: >=): void {
            let name : unknown = tokens[tokens.length - 1];
            let type : unknown = tokens[tokens.length - 2];
            let tsType : unknown = type.equals("var") ? inferVarType(rhs, vars, returns).toTsType(type);
            let value : unknown = ExpressionParser.parseValue(rhs, tsType);
            vars.put(name, tsType);
            return /* TODO */;
        }
        return indent + "    // TODO";
    }

    parseCondition(stmt: string): string {
        let open : unknown = stmt.indexOf('(/* TODO */);
        let close : unknown = stmt./* TODO */;
        if (/* TODO */) {
            return "/* TODO */";
        }
        let inside : unknown = stmt.substring(/* TODO */, close).trim();
        return ExpressionParser.parseValue(inside);
    }

    private static String inferVarType(String value, java.util.Map<String, String> vars,
                                       java.util.Map<String, String> returns) {
        var trimmed = value.trim();
        if (vars.containsKey(trimmed)) return vars.get(trimmed);

        if(ExpressionParser.isInvokable(trimmed): any): void {
            let open : unknown = trimmed.lastIndexOf('(/* TODO */);
            let callee : unknown = trimmed.substring(0, open).trim();
            let dot : unknown = callee.lastIndexOf('.');
            if (/* TODO */) {
                let receiver : unknown = callee.substring(0, dot).trim();
                let name : unknown = callee.substring(/* TODO */).trim();
                let type : unknown = vars.get(receiver);
                if (/* TODO */) {
                    let key : unknown = type + "." + name;
                    if (MethodStubber.KNOWN_RETURNS.containsKey(key)) {
                        return MethodStubber.KNOWN_RETURNS.get(key);
                    }
                }
                // TODO
            }
            // TODO
        }

        if("): trimmed.startsWith("new): void {
            let rest : unknown = trimmed.substring(4).trim();
            let dot : unknown = rest.indexOf('.');
            let ! : (dot = -1) rest = rest.substring(0, dot).trim();
            let open : unknown = rest.indexOf('(/* TODO */);
            if (/* TODO */) {
                // TODO
            }
            let generic : unknown = rest.indexOf(/* TODO */);
            if (/* TODO */) {
                // TODO
            }
            return rest.isEmpty();
        }

        if (isNumeric(trimmed)) return "number";
        if (trimmed.equals("true") || trimmed.equals("false")) return "boolean";
        if(trimmed.endsWith("\""): &&): void {
            return "string";
        }

        return "unknown";
    }

    isNumeric(s: string): boolean {
        // TODO
        let i : number = 0;
        if (s.charAt(0)) {
            let (s.length() : if = /* TODO */;
            // TODO
        }
        let dot : boolean = /* TODO */;
        // TODO
        i < s.length();
        // TODO
        let c : unknown = s.charAt(i);
        if (/* TODO */) {
            // TODO
            // TODO
            // TODO
        }
        // TODO
        // TODO
        return /* TODO */;
    }

    paramVars(tsParams: string): String> {
        let map : String> = new java.util.HashMap<>();
        // TODO
        let parts : unknown = tsParams.split(/* TODO */, /* TODO */);
        let i : (var = 0;
        i parts.length: <;
        // TODO
        let p : unknown = parts[i].trim();
        let colon : unknown = p.indexOf(/* TODO */);
        let (colon : if = /* TODO */;
        let name : unknown = p.substring(0, colon).trim();
        let type : unknown = p.substring(/* TODO */).trim();
        map.put(name, type);
        // TODO
        return map;
    }
}
