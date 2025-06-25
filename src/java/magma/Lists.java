package magma;

public class Lists {
    public static <Value> ListLike<Value> empty() {
        return new JavaList<>();
    }
}
