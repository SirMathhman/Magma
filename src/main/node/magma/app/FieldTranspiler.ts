export default class FieldTranspiler {
    transpileFields(source: string): string {
        let lines: string[] = source.split("\\R");
        let out: any = new StringBuilder();
        // TODO
        let trimmed: string = line.trim();
        // TODO
        ") || trimmed.contains("(") || trimmed.startsWith("import");
        /* */: any;
        out.append(line).append(System.lineSeparator());
        // TODO
        // TODO
        let indent: string = line.substring(0, line.indexOf(trimmed));
        let withoutSemi: string = trimmed.substring(0, trimmed.length()).trim();
        let eq: number = withoutSemi.indexOf(/* TODO */);
        if (/* TODO */) {
            // TODO
        }
        let tokens: string[] = withoutSemi.split("\\s+");
        if (tokens.length < 2) {
            // TODO
        }
        let name: string = tokens[tokens.length - 1];
        let type: string = tokens[tokens.length - 2];
        let modArray: string[] = java.util.Arrays.copyOf(tokens, tokens.length - 2);
        let modifiers: string = replaceFinalWithReadonly(modArray);
        let tsType: string = TypeMapper.toTsType(type);
        out.append(indent);
        if (!modifiers.isBlank()) {
            // TODO
        }
        out.append(name).append(": ").append(tsType);
        // TODO
        .append(System.lineSeparator());
        // TODO
        return out.toString().trim();
    }

    replaceFinalWithReadonly(mods: string[]): string {
        let i: any = 0;
        i mods.length: any;
        // TODO
        if (mods[i].equals("final")) {
            // TODO
        }
        // TODO
        return String.join(" ", mods).trim();
    }
}
