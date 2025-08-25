package magma.result;

public record Ok<Value, Error>(Value value) implements Result<Value, Error> {}
