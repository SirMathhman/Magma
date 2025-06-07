export default class TypeMapper {
    toTsParams(params: string): string {
        if (/* TODO */) {
            // TODO
        }
        let out: java.util.List<string> = new java.util.ArrayList<>();
        // TODO
        let parts: string[] = p./* TODO */()./* TODO */("\\s+");
        let (parts.length: any = /* TODO */;
        let name: string = parts[parts.length - 1];
        let type: string = parts.length > 1 ? parts[parts.length - 2] : "any";
        /* TODO */(/* TODO */);
        // TODO
        return /* TODO */;
    }

    toTsType(javaType: string): string {
        let genericStart: number = javaType./* TODO */(/* TODO */);
        let genericEnd: number = javaType./* TODO */(/* TODO */);
        if (/* TODO */) {
            // TODO
        }
        if (/* TODO */) {
            // TODO
        }
        return /* TODO */;
        // TODO
        // TODO
        // TODO
        // TODO
        // TODO
        // TODO
    }

    mapGeneric(javaType: string, start: number, end: number): string {
        let base: string = javaType./* TODO */(0, /* TODO */)./* TODO */();
        let params: string = javaType./* TODO */(/* TODO */, /* TODO */);
        let mapped: java.util.List<string> = new java.util.ArrayList<>();
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        return /* TODO */;
    }
}
