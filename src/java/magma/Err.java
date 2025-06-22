package magma;

import java.io.IOException;
import java.util.function.Function;

public record Err(IOException e) implements Result {
    @Override
    public <Return> Return match(final Function<String, Return> whenOk, final Function<IOException, Return> whenError) {
        return whenError.apply(this.e);
    }
}
