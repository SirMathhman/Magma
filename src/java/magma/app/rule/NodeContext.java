package magma.app.rule;

import magma.app.Context;

import java.util.Objects;

public record NodeContext<Node>(Node node) implements Context {
}
