package jvm.list;

import magma.api.list.ListLike;
import magma.api.list.Sequence;

import java.util.Arrays;

public class JVMLists {
    public static <T> ListLike<T> empty() {
        return new JVMList<>();
    }

    @SafeVarargs
    public static <T> Sequence<T> of(T... elements) {
        return new JVMList<>(Arrays.asList(elements));
    }
}
