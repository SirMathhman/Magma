export default class TypeMapper {
    toTsParams(params: string): string {
        if (params.isBlank()) {
            // TODO
        }
        let out: java.util.List<string> = new java.util.ArrayList<>();
        // TODO
        let parts: string[] = p.trim().split("\\s+");
        let (parts.length: any = /* TODO */;
        let name: string = parts[parts.length - 1];
        let type: string = parts.length > 1 ? parts[parts.length - 2] : "any";
        out.add(name + ": " + toTsType(type));
        // TODO
        return String.join(/* TODO */, /* TODO */, out);
    }

    toTsType(javaType: string): string {
        let genericStart: number = javaType.indexOf(/* TODO */);
        let genericEnd: number = javaType.lastIndexOf(/* TODO */);
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
        let base: string = javaType.substring(0, start).trim();
        let params: string = javaType.substring(/* TODO */, end);
        let mapped: java.util.List<string> = new java.util.ArrayList<>();
        // TODO
        mapped.add(toTsType(p.trim()));
        // TODO
        return base + "<" + String.join(/* TODO */, /* TODO */, mapped);
    }
}
