import JdkList from "../list/JdkList";
import ListLike from "../list/ListLike";
export default class TypeMapper {
    toTsParams(params: string): string {
        if (params.isBlank()) {
            return "";
        }
        let pieces: ListLike<string> = split(params);
        let out: ListLike<string> = JdkList.create();
        let i: (var = 0;
        i < pieces.size();
        // TODO
        let p: var = pieces.get(i);
        let parts: var = p.trim().split("\\s+");
        let (parts.length: if = /* TODO */;
        let name: var = parts[parts.length - 1];
        let type: var = parts.length > 1 ? parts[parts.length - 2] : "any";
        out.add(name + ": " + toTsType(type));
        // TODO
        let result: var = new StringBuilder();
        let i: (var = 0;
        i < out.size();
        // TODO
        // TODO
        result.append(out.get(i));
        // TODO
        return result.toString();
    }

    toTsType(javaType: string): string {
        let genericStart: var = javaType.indexOf(/* TODO */);
        let genericEnd: var = javaType.lastIndexOf(/* TODO */);
        if (/* TODO */) {
            let base: var = javaType.substring(0, genericStart).trim();
            let name: var = base.substring(base.lastIndexOf('.'));
            let params: var = javaType.substring(/* TODO */, genericEnd);
            switch(name);
            // TODO
            return mapFunction(params);
            // TODO
            // TODO
            return mapBiFunction(params);
            // TODO
            // TODO
            let ts: var = mapParams(params, 0);
            return "();
            // TODO
            // TODO
            let ts: var = mapParams(params, 1);
            return "(" + ts.params + ");
            // TODO
            // TODO
            let ts: var = mapParams(params, 2);
            return "(" + ts.params + ");
            // TODO
            // TODO
            let ts: var = mapParams(params, 1);
            return "(" + ts.params + ");
            // TODO
            // TODO
            let ts: var = mapParams(params, 1);
            return "(" + ts.params + ");
            // TODO
            // TODO
            let ts: var = mapParams(params, 2);
            return "(" + ts.params + ");
            // TODO
            // TODO
            return mapGeneric(javaType, genericStart, genericEnd);
            // TODO
            // TODO
        }
        if (javaType.endsWith("[]")) {
            let element: var = javaType.substring(0, javaType.length());
            return toTsType(element);
        }
        return switch(javaType);
        // TODO
        // TODO
        // TODO
        // TODO
        // TODO
        // TODO
        // TODO
        // TODO
    }

    mapGeneric(javaType: string, start: number, end: number): string {
        let base: var = javaType.substring(0, start).trim();
        let params: var = javaType.substring(/* TODO */, end);
        let mapped: ListLike<string> = JdkList.create();
        let pieces: var = split(params);
        let i: (var = 0;
        i < pieces.size();
        // TODO
        mapped.add(toTsType(pieces.get(i).trim()));
        // TODO
        let joined: var = new StringBuilder();
        let i: (var = 0;
        i < mapped.size();
        // TODO
        // TODO
        joined.append(mapped.get(i));
        // TODO
        return /* TODO */;
    }

    split(text: string): ListLike<string> {
        let out: ListLike<string> = JdkList.create();
        let depth: var = 0;
        let part: var = new StringBuilder();
        let i: (var = 0;
        i < text.length();
        // TODO
        let c: var = text.charAt(i);
        let (c: if = /* TODO */;
        let (c: if = /* TODO */;
        if (/* TODO */) {
            out.add(part.toString());
            part.setLength(0);
            // TODO
        }
        part.append(c);
        // TODO
        out.add(part.toString());
        return out;
    }

    private static record Params(String params, String returnType) {}

    mapParams(params: string, paramCount: number): Params {
        let out: ListLike<string> = JdkList.create();
        let parts: var = split(params);
        let i: (var = 0;
        i < Math.min(paramCount, parts.size());
        // TODO
        out.add("arg" + i + ": " + toTsType(parts.get(i).trim()));
        // TODO
        let ret: var = parts.size() > paramCount ? toTsType(parts.get(parts.size()).trim());
        let joined: var = new StringBuilder();
        let i: (var = 0;
        i < out.size();
        // TODO
        // TODO
        joined.append(out.get(i));
        // TODO
        return new Params(joined.toString(), ret);
    }

    mapFunction(params: string): string {
        let ts: var = mapParams(params, 1);
        return "(" + ts.params + ");
    }

    mapBiFunction(params: string): string {
        let ts: var = mapParams(params, 2);
        return "(" + ts.params + ");
    }
}
