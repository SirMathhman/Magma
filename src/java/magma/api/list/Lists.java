package magma.api.list;

public class Lists {
    public static <T> ListLike<T> empty() {
        return new JVMList<>();
    }
}
