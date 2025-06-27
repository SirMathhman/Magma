package magma;

public class Lists {
    public static <String> ListLike<String> empty() {
        return new JavaList<>();
    }
}
