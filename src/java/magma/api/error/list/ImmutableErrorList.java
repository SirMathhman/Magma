package magma.api.error.list;

import magma.api.list.ListLike;
import magma.api.list.ListLikes;

import java.util.function.Function;
import java.util.stream.Collectors;

public record ImmutableErrorList<Error>(ListLike<Error> errors) implements ErrorList<Error> {
    public ImmutableErrorList() {
        this(ListLikes.empty());
    }

    @Override
    public String join(final Function<Error, String> mapper) {
        return errors.stream()
                .map(mapper)
                .collect(Collectors.joining());
    }

    @Override
    public ErrorList<Error> add(final Error error) {
        return new ImmutableErrorList<>(errors.add(error));
    }
}
