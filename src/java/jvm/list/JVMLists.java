package jvm.list;

import magma.api.list.ListLike;

import java.util.Arrays;

public class JVMLists {
    public static <T> ListLike<T> empty() {
        return new JVMList<>();
    }

    public static <T> ListLike<T> of(T... elements) {
        return new JVMList<>(Arrays.asList(elements));
    }
}
