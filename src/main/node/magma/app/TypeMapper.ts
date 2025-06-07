import ArrayList from "../../java/util/ArrayList";
import List from "../../java/util/List";
export default class TypeMapper {
    toTsParams(params: string): string {
        if (params.isBlank()) {
            return "";
        }
        let out: List<string> = new ArrayList<>();
        // TODO
        let parts: var = p.trim().split("\\s+");
        let (parts.length: if = /* TODO */;
        let name: var = parts[parts.length - 1];
        let type: var = parts.length > 1 ? parts[parts.length - 2] : "any";
        out.add(name + ": " + toTsType(type));
        // TODO
        return String.join(/* TODO */, /* TODO */, out);
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
        let mapped: List<string> = new ArrayList<>();
        // TODO
        mapped.add(toTsType(p.trim()));
        // TODO
        return base + "<" + String.join(/* TODO */, /* TODO */, mapped);
    }
}
