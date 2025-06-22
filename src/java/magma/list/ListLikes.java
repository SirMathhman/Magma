package magma.list;

import java.util.ArrayList;
import java.util.Arrays;

public class ListLikes {
    private ListLikes() {
    }

    @SafeVarargs
    public static <T> ListLike<T> of(final T... elements) {
        return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
    }

    public static <T> ListLike<T> empty() {
        return new JavaList<>();
    }
}
