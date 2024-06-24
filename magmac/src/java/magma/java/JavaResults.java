package magma.java;

import magma.api.option.Option;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

public class JavaResults {
    public static <E extends Throwable> void $(Option<E> option) throws E {
        if (option.isPresent()) {
            throw option.orElsePanic();
        }
    }

    public static <T, E extends Throwable> T $(Result<T, E> result) throws E {
        var value = JavaOptionals.toNative(result.findValue());
        if (value.isPresent()) {
            return value.orElseThrow();
        }

        var error = JavaOptionals.toNative(result.findErr());
        if (error.isPresent()) {
            throw error.get();
        }

        throw new RuntimeException("Neither a value nor an error is present. This result contains nothing.");
    }

    public static <T, E extends Throwable> Result<T, E> $(JavaAction<T, E> action) {
        try {
            return new Ok<>(action.execute());
        } catch (Throwable e) {
            //noinspection unchecked
            return new Err<>((E) e);
        }
    }
}
