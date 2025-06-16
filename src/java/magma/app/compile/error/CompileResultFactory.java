package magma.app.compile.error;

public interface CompileResultFactory {
    <Value> CompileResult<Value> fromValue(Value input);

    <Value> CompileResult<Value> fromStringError(String message, String input);
}
