package magma.app.result;

import magma.app.Generated;

public class EmptyGenerated implements Generated {
    @Override
    public StringBuilder appendTo(StringBuilder cache) {
        return cache;
    }
}
