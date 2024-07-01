package magma.build.compile.error;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class TimeoutError implements Error_ {
    private final String context;
    private final Duration duration;

    public TimeoutError(String context, Duration duration) {
        this.context = context;
        this.duration = duration;
    }

    @Override
    public Optional<String> findMessage() {
        return Optional.of("Timeout after: " + duration.toMillis() + " ms");
    }

    @Override
    public Optional<List<Error_>> findCauses() {
        return Optional.empty();
    }

    @Override
    public Optional<String> findContext() {
        return Optional.of(context);
    }

    @Override
    public int calculateDepth() {
        return 0;
    }
}
