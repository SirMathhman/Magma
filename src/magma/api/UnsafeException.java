package magma.api;

public class UnsafeException extends Exception {
    private final Object value;

    public UnsafeException(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
