package magma;

public class Maps {
    private Maps() {
    }

    public static <Key, Value> MapLike<Key, Value> empty() {
        return new JavaMap<>();
    }
}
