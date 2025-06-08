import Arrays from "../../java/util/Arrays";
export default class FieldTranspiler {
    transpileFields(source: string): string {
        let lines : unknown = source.split("\\R");
        let out : StringBuilder = new StringBuilder();
        // TODO
        let trimmed : unknown = line.trim();
        // TODO
        ") || trimmed.contains("(") || trimmed.startsWith("import");
        /* */: TODO;
        out.append(line).append(System.lineSeparator());
        // TODO
        // TODO
        let indent : unknown = line.substring(0, line.indexOf(trimmed));
        let withoutSemi : unknown = trimmed.substring(0, trimmed.length()).trim();
        let eq : unknown = withoutSemi.indexOf(/* TODO */);
        if (/* TODO */) {
            // TODO
        }
        let tokens : unknown = withoutSemi.split("\\s+");
        if (tokens.length < 2) {
            out.append(line).append(System.lineSeparator());
            // TODO
        }
        let name : unknown = tokens[tokens.length - 1];
        let type : unknown = tokens[tokens.length - 2];
        let modArray : unknown = Arrays.copyOf(tokens, tokens.length - 2);
        let modifiers : unknown = replaceFinalWithReadonly(modArray);
        let tsType : unknown = TypeMapper.toTsType(type);
        out.append(indent);
        if (!modifiers.isBlank()) {
            out.append(modifiers).append(" ");
        }
        out.append(name).append(": ").append(tsType);
        // TODO
        .append(System.lineSeparator());
        // TODO
        return out.toString().trim();
    }

    replaceFinalWithReadonly(mods: string[]): string {
        let i : (var = 0;
        i mods.length: <;
        // TODO
        if (mods[i].equals("final")) {
            // TODO
        }
        // TODO
        return String.join(" ", mods).trim();
    }
}
