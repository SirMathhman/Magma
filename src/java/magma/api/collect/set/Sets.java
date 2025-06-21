package magma.api.collect.set;

class Sets {
    private Sets() {
    }

    public static <Value> SetLike<Value> empty() {
        return new JavaSet<>();
    }
}
