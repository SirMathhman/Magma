package magma.app.compile.define;

import magma.app.compile.value.Caller;

public record ConstructionCaller(String right) implements Caller {
    @Override
    public String generate() {
        return "new " + this.right;
    }

}
