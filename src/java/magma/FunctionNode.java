package magma;

import magma.node.Header;

public record FunctionNode(Header header, String params, String content) {
    String generate() {
        return this.header()
                .generate() + "(" + this.params() + ") {" + this.content() + Strings.LINE_SEPARATOR + "}" + Strings.LINE_SEPARATOR;
    }
}