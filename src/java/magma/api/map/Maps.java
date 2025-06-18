package magma.api.map;

public class Maps {
    public static <K, V> MapLike<K, V> empty() {
        return new JVMMap<>();
    }
}
