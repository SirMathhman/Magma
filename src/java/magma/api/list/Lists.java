package magma.api.list;

import java.util.Arrays;

public class Lists {
    public static <T> ListLike<T> empty() {
        return new JVMList<>();
    }

    public static <T> ListLike<T> of(T... elements) {
        return new JVMList<>(Arrays.asList(elements));
    }
}
