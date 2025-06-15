package magma.app.rule;

import magma.app.Context;

public record StringContext(String input) implements Context {
    @Override
    public String display() {
        return this.input;
    }
}
