package magma.result;

public record Err<Value, Error>(Error error) implements Result<Value, Error> {}
