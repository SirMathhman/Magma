import JdkList from "../list/JdkList";
import ListLike from "../list/ListLike";
export default class ExpressionParser {
    isMemberAccess(stmt: string): boolean {
        return stmt.contains(".") && !stmt.contains("(") && !stmt.contains("=");
    }

    parseMemberAccess(stmt: string, indent: string): string {
        return /* TODO */;
    }

    isInvokable(stmt: string): boolean {
        let open : unknown = stmt.indexOf('(/* TODO */);
        let close : unknown = stmt./* TODO */;
        if (/* TODO */) {
            return /* TODO */;
        }
        let arrow : unknown = stmt.indexOf("=>");
        if (/* TODO */) {
            return /* TODO */;
        }
        let head : unknown = stmt.substring(0, open).trim();
        // TODO
        return !head.startsWith("if").startsWith("while").startsWith("for");
    }

    parseInvokable(stmt: string, indent: string): string {
        return indent + "    " + stubInvokableExpr(stmt);
    }

    parseValue(value: string, expected: string): string {
        let trimmed : unknown = value.trim();
        if (trimmed.startsWith("new ").contains("<>")) {
            // TODO
        }
        // TODO
        if (trimmed.startsWith("!")) {
            let rest : unknown = trimmed.substring(1).trim();
            return "!" + parseValue(rest, expected);
        }
        if (trimmed.startsWith("new ").contains(".") && isInvokable(trimmed)) {
            return trimmed;
        }
        if (trimmed.contains(".") && !trimmed.contains("=")) {
            return parseMemberChain(trimmed);
        }
        if (isInvokable(trimmed)) {
            return stubInvokableExpr(trimmed);
        }
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed;
        }
        if (isMemberAccess(trimmed) || isNumeric(trimmed)) {
            return trimmed;
        }
        if (isIdentifier(trimmed)) {
            return trimmed;
        }
        if (isNumeric(trimmed)) {
            return trimmed;
        }
        return "/* TODO */";
    }

    parseValue(value: string): string {
        return parseValue(value, /* TODO */);
    }

    stubInvokableExpr(stmt: string): string {
        let close : unknown = stmt./* TODO */;
        let (close : if = /* TODO */;
        let open : number = findOpenParen(stmt, close);
        let (open : if = /* TODO */;
        let callee : unknown = stmt.substring(0, open).trim();
        let args : unknown = stmt.substring(/* TODO */, close).trim();
        let parts : ListLike<string> = splitArgs(args);
        mapArgs(parts);
        let joined : string = joinArgs(parts);
        return callee + "(" + joined + ");
    }

    parseValueArg(value: string): string {
        let trimmed : unknown = value.trim();
        return parseValue(trimmed, /* TODO */);
    }

    fillDiamond(expr: string, expected: string): string {
        let (expected : if = /* TODO */;
        let start : unknown = expected.indexOf(/* TODO */);
        let end : unknown = expected.lastIndexOf(/* TODO */);
        let (start : if = /* TODO */;
        let generic : unknown = expected.substring(/* TODO */, end);
        return expr.replace("<>", "<" + generic + ">");
    }

    parseMemberChain(expr: string): string {
        let parts : ListLike<string> = splitMemberParts(expr);
        return joinMemberParts(parts);
    }

    splitMemberParts(expr: string): ListLike<string> {
        let parts : ListLike<string> = JdkList.create();
        let depth : number = 0;
        let part : StringBuilder = new StringBuilder();
        let i : (var = 0;
        i < expr.length();
        // TODO
        let c : unknown = expr.charAt(i);
        if (/* TODO */) {
            parts.add(part.toString());
            part.setLength(0);
            // TODO
        }
        let (c : if = = '(/* TODO */);
        let (c : if = /* TODO */;
        part.append(c);
        // TODO
        parts.add(part.toString());
        return parts;
    }

    joinMemberParts(parts: ListLike<string>): string {
        let out : StringBuilder = new StringBuilder();
        let i : (var = 0;
        i < parts.size();
        // TODO
        // TODO
        out.append(parseChainSegment(parts.get(i).trim()));
        // TODO
        return out.toString();
    }

    parseChainSegment(seg: string): string {
        if (isInvokable(seg)) {
            return stubInvokableExpr(seg);
        }
        return seg;
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

    isIdentifier(s: string): boolean {
        // TODO
        // TODO
        let first : unknown = s.charAt(0);
        if (!/* TODO */) {
            return /* TODO */;
        }
        let i : (var = 1;
        i < s.length();
        // TODO
        let c : unknown = s.charAt(i);
        let letter : unknown = /* TODO */;
        let digit : unknown = /* TODO */;
        if (!/* TODO */) {
            return /* TODO */;
        }
        // TODO
        return /* TODO */;
    }

    findOpenParen(stmt: string, close: number): number {
        let depth : number = 0;
        let i : (var = close;
        let > : i = 0;
        // TODO
        let c : unknown = stmt.charAt(i);
        if (/* TODO */) {
            // TODO
            let (depth : if = /* TODO */;
        }
        // TODO
        return -1;
    }

    mapArgs(parts: ListLike<string>): void {
        let i : (var = 0;
        i < parts.size();
        // TODO
        parts.set(i, parseValueArg(parts.get(i)));
        // TODO
    }

    joinArgs(parts: ListLike<string>): string {
        let out : StringBuilder = new StringBuilder();
        let i : (var = 0;
        i < parts.size();
        // TODO
        // TODO
        out.append(parts.get(i));
        // TODO
        return out.toString();
    }

    splitArgs(args: string): ListLike<string> {
        let out : ListLike<string> = JdkList.create();
        // TODO
        let depth : number = 0;
        let part : StringBuilder = new StringBuilder();
        let i : (var = 0;
        i < args.length();
        // TODO
        let c : unknown = args.charAt(i);
        if (/* TODO */) {
            out.add(part.toString().trim());
            part.setLength(0);
            // TODO
        }
        let (c : if = = '(/* TODO */);
        let (c : if = /* TODO */;
        part.append(c);
        // TODO
        out.add(part.toString().trim());
        return out;
    }
}
