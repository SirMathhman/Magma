package magma;

import java.io.IOException;
import java.util.function.Function;

public record Ok<String>(String value) implements Result<String> {
    @Override
    public <Return> Return match(final Function<String, Return> whenOk, final Function<IOException, Return> whenError) {
        return whenOk.apply(this.value);
    }

    @Override
    public <Return> Result<Return> map(final Function<String, Return> mapper) {
        return new Ok<>(mapper.apply(this.value));
    }
}
