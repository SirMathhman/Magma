package magma.list;

public class Lists {
    private Lists() {
    }

    public static <Value> ListLike<Value> empty() {
        return new JavaList<>();
    }
}
