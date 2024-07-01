package magma.build.compile.parse.result;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Err;
import magma.api.result.Result;
import magma.build.compile.attribute.Attributes;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;

import java.time.Duration;
import java.util.function.Function;

public record ErrorParsingResult(Error_ e, Option<Duration> duration) implements ParsingResult {
    public ErrorParsingResult(Error_ e) {
        this(e, new None<>());
    }

    @Override
    public ParsingResult mapErr(Function<Error_, Error_> mapper) {
        return new ErrorParsingResult(mapper.apply(e), duration);
    }

    @Override
    public ParsingResult withType(String type) {
        return this;
    }

    @Override
    public Option<Error_> findError() {
        return new Some<>(e);
    }

    @Override
    public Option<Attributes> findAttributes() {
        return new None<>();
    }

    @Override
    public Option<Node> tryCreate() {
        return new None<>();
    }

    @Override
    public Option<Duration> findDuration() {
        return duration;
    }

    @Override
    public ParsingResult withDuration(Duration duration) {
        return new ErrorParsingResult(e, new Some<>(duration));
    }

    @Override
    public Result<Node, Error_> create() {
        return new Err<>(e);
    }
}
