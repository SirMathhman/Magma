package magma.build.compile.error;

import magma.api.option.None;
import magma.api.option.Option;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public record CompileError(String message, String context) implements Error_ {
    @Override
    public Optional<String> findMessage() {
        return Optional.of(message);
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
        return 1;
    }

    @Override
    public Option<Duration> findDuration() {
        return new None<>();
    }
}
