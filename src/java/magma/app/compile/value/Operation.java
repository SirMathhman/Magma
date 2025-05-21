package magma.app.compile.value;

public record Operation(Value left, String targetInfix, Value right) implements Value {

}
