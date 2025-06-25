package magma;

import magma.node.CHeader;
import magma.node.CParameter;

import java.util.List;
import java.util.stream.Collectors;

public record CFunction(CHeader header, List<CParameter> params, String content) {
    String generate() {
        final var objectStream = this.params.stream()
                .map(CParameter::generate)
                .collect(Collectors.joining(", "));

        return this.header.generate() + "(" + objectStream + ") {" + this.content() + Strings.LINE_SEPARATOR + "}" + Strings.LINE_SEPARATOR;
    }
}