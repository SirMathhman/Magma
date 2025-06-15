package magma.app.result;

import magma.app.Result;

public record PresentResult(String output) implements Result {
    @Override
    public StringBuilder appendTo(StringBuilder cache) {
        return cache.append(this.output);
    }
}
