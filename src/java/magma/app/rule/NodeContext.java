package magma.app.rule;

import magma.app.Context;
import magma.app.Node;

public record NodeContext(Node node) implements Context {
}
