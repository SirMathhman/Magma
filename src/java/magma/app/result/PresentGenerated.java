package magma.app.result;

import magma.app.Generated;

public record PresentGenerated(String output) implements Generated {
    @Override
    public StringBuilder appendTo(StringBuilder cache) {
        return cache.append(this.output);
    }
}
