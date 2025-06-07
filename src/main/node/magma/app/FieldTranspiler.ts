import Arrays from "../../java/util/Arrays";
export default class FieldTranspiler {
    transpileFields(source: string): string {
        let lines: any = source.split("\\R");
        let out: any = new StringBuilder();
        // TODO
        let trimmed: any = line.trim();
        // TODO
        ") || trimmed.contains("(") || trimmed.startsWith("import");
        /* */: any;
        out.append(line).append(System.lineSeparator());
        // TODO
        // TODO
        let indent: any = line.substring(0, line.indexOf(trimmed));
        let withoutSemi: any = trimmed.substring(0, trimmed.length()).trim();
        let eq: any = withoutSemi.indexOf(/* TODO */);
        if (/* TODO */) {
            // TODO
        }
        let tokens: any = withoutSemi.split("\\s+");
        if (tokens.length < 2) {
            // TODO
        }
        let name: any = tokens[tokens.length - 1];
        let type: any = tokens[tokens.length - 2];
        let modArray: any = Arrays.copyOf(tokens, tokens.length - 2);
        let modifiers: any = replaceFinalWithReadonly(modArray);
        let tsType: any = TypeMapper.toTsType(type);
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
