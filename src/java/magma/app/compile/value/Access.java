package magma.app.compile.value;

import magma.app.compile.node.Node;

public record Access(Node child, String property) implements Node {
}
