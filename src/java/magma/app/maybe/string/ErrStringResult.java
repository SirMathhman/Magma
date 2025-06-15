package magma.app.maybe.string;

import magma.app.CompileError;
import magma.app.maybe.StringResult;
import magma.app.rule.or.OrState;

public class ErrStringResult implements StringResult {
    private final CompileError error;

    public ErrStringResult(CompileError error) {
        this.error = error;
    }

    @Override
    public String orElse(String other) {
        return other;
    }

    @Override
    public OrState<String> attachTo(OrState<String> state) {
        return state.withError(this.error);
    }

    @Override
    public StringResult appendString(String other) {
        return this;
    }

    @Override
    public StringResult appendMaybe(StringResult other) {
        return this;
    }

    @Override
    public StringResult prependString(String other) {
        return this;
    }
}
