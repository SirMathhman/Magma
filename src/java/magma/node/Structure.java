package magma.node;

import magma.Strings;

public record Structure(String beforeKeyword, String name, String output) {
    public String generate() {
        return Placeholder.generate(this.beforeKeyword()) + "struct " + this.name() + " {" + this.output() + "};" + Strings.LINE_SEPARATOR;
    }
}