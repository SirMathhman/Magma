package jv.api.result;

import jv.api.JavaOptions;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class JavaResults {
    public static <T, X extends Throwable> Result<T, X> wrap(ThrowableSupplier<T, X> supplier) {
        try {
            return new Ok<>(supplier.get());
        } catch (Throwable e) {
            //noinspection unchecked
            return new Err<>((X) e);
        }
    }

    public static <T, X extends Throwable> T unwrap(Result<T, X> result) throws X {
        Optional<T> maybeValue = JavaOptions.unwrap(result.findValue());
        if (maybeValue.isPresent()) return maybeValue.get();

        Optional<X> maybeError = JavaOptions.unwrap(result.findError());
        if (maybeError.isPresent()) throw maybeError.get();

        throw new RuntimeException("Neither a value nor an error is present.");
    }

    public static String convertThrowableToString(IOException exception) {
        StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
