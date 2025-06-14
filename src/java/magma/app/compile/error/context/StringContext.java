package magma.app.compile.error.context;

import magma.app.compile.error.Context;

public record StringContext(String value) implements Context {
    @Override
    public String display() {
        return this.value;
    }
}
