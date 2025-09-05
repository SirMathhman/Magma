package magma;

public class Interpreter {
    public Result<String, InterpretError> interpret(String input) {
        if (input == null) {
            return new Err<>(new InterpretError("input is null"));
        }
        if (input.equals("")) {
            return new Ok<>("");
        }
        // simple behaviour: echo input
        return new Ok<>(input);
    }
}
