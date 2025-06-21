package magma.app.list;

import jvm.JavaList;

public class Lists {
    private Lists() {
    }

    public static <T> ListLike<T> empty() {
        return new JavaList<>();
    }
}
