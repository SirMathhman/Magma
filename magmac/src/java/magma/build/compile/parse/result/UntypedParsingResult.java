package magma.build.compile.parse.result;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Err;
import magma.api.result.Result;
import magma.build.compile.attribute.Attributes;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;

import java.time.Duration;
import java.util.function.Function;

public record UntypedParsingResult(Attributes attributes, Option<Duration> duration) implements ParsingResult {
    public UntypedParsingResult(Attributes attributes) {
        this(attributes, new None<>());
    }

    @Override
    public ParsingResult withType(String type) {
        return new TypedParsingResult(type, attributes);
    }

    @Override
    public ParsingResult mapErr(Function<Error_, Error_> mapper) {
        return this;
    }

    @Override
    public ParsingResult withDuration(Duration duration) {
        return new UntypedParsingResult(attributes, new Some<>(duration));
    }

    @Override
    public Option<Duration> findDuration() {
        return duration;
    }

    @Override
    public Option<Error_> findError() {
        return new None<>();
    }

    @Override
    public Option<Attributes> findAttributes() {
        return new Some<>(attributes);
    }

    @Override
    public Option<Node> tryCreate() {
        return new None<>();
    }

    @Override
    public Result<Node, Error_> create() {
        return new Err<>(new CompileError("Neither value nor error is present.", ""));
    }
}
