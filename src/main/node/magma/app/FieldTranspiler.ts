export default class FieldTranspiler {
    transpileFields(source: string): string {
        let lines: string[] = source./* TODO */("\\R");
        let out: any = new StringBuilder();
        // TODO
        let trimmed: string = line./* TODO */();
        // TODO
        /* TODO */("import");
        /* */: any;
        /* TODO */(/* TODO */);
        // TODO
        // TODO
        let indent: string = line./* TODO */(0, /* TODO */);
        let withoutSemi: string = trimmed./* TODO */(0, /* TODO */)./* TODO */();
        let eq: number = /* TODO */(/* TODO */);
        if (/* TODO */) {
            // TODO
        }
        let tokens: string[] = withoutSemi./* TODO */("\\s+");
        if (tokens.length < 2) {
            // TODO
        }
        let name: string = tokens[tokens.length - 1];
        let type: string = tokens[tokens.length - 2];
        let modArray: string[] = java.util.Arrays./* TODO */(/* TODO */, tokens.length - 2);
        let modifiers: string = /* TODO */(/* TODO */);
        let tsType: string = TypeMapper./* TODO */(/* TODO */);
        /* TODO */(/* TODO */);
        if (!modifiers./* TODO */()) {
            // TODO
        }
        /* TODO */(/* TODO */);
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        return out./* TODO */()./* TODO */();
    }

    replaceFinalWithReadonly(mods: string[]): string {
        let i: any = 0;
        i mods.length: any;
        // TODO
        if (mods[i]./* TODO */("final")) {
            // TODO
        }
        // TODO
        return String./* TODO */(" ", /* TODO */)./* TODO */();
    }
}
