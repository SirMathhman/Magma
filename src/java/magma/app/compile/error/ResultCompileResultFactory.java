package magma.app.compile.error;

import magma.api.Err;
import magma.api.Ok;
import magma.app.compile.context.StringContext;

public class ResultCompileResultFactory implements CompileResultFactory {
    private ResultCompileResultFactory() {
    }

    public static CompileResultFactory createResultCompileResultFactory() {
        return new ResultCompileResultFactory();
    }

    @Override
    public <Value> CompileResult<Value> fromValue(Value input) {
        return new ResultCompileResult<>(new Ok<>(input));
    }

    @Override
    public <Value> CompileResult<Value> fromStringError(String message, String input) {
        return new ResultCompileResult<>(new Err<>(new CompileError(message, new StringContext(input))));
    }
}
