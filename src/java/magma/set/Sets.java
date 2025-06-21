package magma.set;

public class Sets {
    public static <Value> SetLike<Value> empty() {
        return new JavaSet<>();
    }
}
