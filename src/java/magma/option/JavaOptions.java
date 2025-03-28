package magma.option;

import java.util.Optional;

public class JavaOptions {
    public static <T> Optional<T> unwrap(Option<T> option) {
        return option.map(Optional::of).orElseGet(Optional::empty);
    }

    public static <T> Option<T> wrap(Optional<T> optional) {
        return optional.<Option<T>>map(Some::new).orElseGet(None::new);
    }
}
