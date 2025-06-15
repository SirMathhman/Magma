package magma.app.compile.rule;

import magma.app.compile.Context;

public record StringContext(String input) implements Context {
    @Override
    public String display() {
        return this.input;
    }
}
