import JdkList from "../list/JdkList";
import ListLike from "../list/ListLike";
export default class TypeMapper {
    toTsParams(params: string): string {
        if (params.isBlank()) {
            return "";
        }
        let out: ListLike<string> = JdkList.create();
        // TODO
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
            return mapGeneric(javaType, genericStart, genericEnd);
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
    }

    mapGeneric(javaType: string, start: number, end: number): string {
        let base: var = javaType.substring(0, start).trim();
        let params: var = javaType.substring(/* TODO */, end);
        let mapped: ListLike<string> = JdkList.create();
        // TODO
        mapped.add(toTsType(p.trim()));
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
}
