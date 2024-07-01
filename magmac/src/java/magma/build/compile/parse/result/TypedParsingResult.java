package magma.build.compile.parse.result;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.attribute.Attributes;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.ImmutableNode;
import magma.build.compile.parse.Node;

import java.time.Duration;
import java.util.function.Function;

public record TypedParsingResult(
        String name,
        Attributes attributes,
        Option<Duration> duration
) implements ParsingResult {
    public TypedParsingResult(String name, Attributes attributes) {
        this(name, attributes, new None<>());
    }

    @Override
    public ParsingResult withType(String type) {
        return this;
    }

    @Override
    public ParsingResult mapErr(Function<Error_, Error_> mapper) {
        return this;
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
        return new Some<>(new ImmutableNode(name, attributes));
    }

    @Override
    public Option<Duration> findDuration() {
        return duration;
    }

    @Override
    public ParsingResult withDuration(Duration duration) {
        return new TypedParsingResult(name, attributes, new Some<>(duration));
    }

    @Override
    public Result<Node, Error_> create() {
        return new Ok<>(new ImmutableNode(name, attributes));
    }
}
