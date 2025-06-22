package magma;

import java.io.IOException;
import java.util.function.Function;

public interface Result {
    <Return> Return match(Function<String, Return> whenOk, Function<IOException, Return> whenError);
}
