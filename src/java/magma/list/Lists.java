package magma.list;

public class Lists {
    private Lists() {
    }

    public static <T> ListLike<T> empty() {
        return new JavaList<>();
    }
}
