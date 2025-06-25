package magma;

import magma.node.CHeader;

public record CFunction(CHeader header, String params, String content) {
    String generate() {
        return this.header()
                .generate() + "(" + this.params() + ") {" + this.content() + Strings.LINE_SEPARATOR + "}" + Strings.LINE_SEPARATOR;
    }
}