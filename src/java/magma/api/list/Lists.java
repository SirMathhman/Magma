package magma.api.list;

import java.util.ArrayList;
import java.util.Arrays;

public class Lists {
    public static <T> ListLike<T> empty() {
        return new JVMList<>();
    }

    @SafeVarargs
    public static <T> ListLike<T> of(T... elements) {
        return new JVMList<>(new ArrayList<>(Arrays.asList(elements)));
    }
}
