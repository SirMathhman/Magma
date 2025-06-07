import ArrayList from "../../java/util/ArrayList";
import List from "../../java/util/List";
export default class TypeMapper {
    toTsParams(params: string): string {
        if (params.isBlank()) {
            // TODO
        }
        let out: List<string> = new ArrayList<>();
        // TODO
        let parts: any = p.trim().split("\\s+");
        let (parts.length: any = /* TODO */;
        let name: any = parts[parts.length - 1];
        let type: any = parts.length > 1 ? parts[parts.length - 2] : "any";
        out.add(name + ": " + toTsType(type));
        // TODO
        return String.join(/* TODO */, /* TODO */, out);
    }

    toTsType(javaType: string): string {
        let genericStart: any = javaType.indexOf(/* TODO */);
        let genericEnd: any = javaType.lastIndexOf(/* TODO */);
        if (/* TODO */) {
            // TODO
        }
        if (javaType.endsWith("[]")) {
            // TODO
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
        let base: any = javaType.substring(0, start).trim();
        let params: any = javaType.substring(/* TODO */, end);
        let mapped: List<string> = new ArrayList<>();
        // TODO
        mapped.add(toTsType(p.trim()));
        // TODO
        return base + "<" + String.join(/* TODO */, /* TODO */, mapped);
    }
}
