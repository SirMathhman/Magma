package magma;

class Maps {
    public static <K, V> Map<K, V> empty() {
        return new JavaMap<>();
    }
}
