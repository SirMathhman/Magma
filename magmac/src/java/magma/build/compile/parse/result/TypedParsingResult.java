package magma.build.compile.parse.result;

import magma.api.option.Option;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.attribute.Attributes;
import magma.build.compile.parse.ImmutableNode;
import magma.build.compile.parse.Node;
import magma.build.java.JavaOptionals;

import java.util.Optional;
import java.util.function.Function;

public record TypedParsingResult(String name, Attributes attributes) implements ParsingResult {

    private Optional<Attributes> findAttributes0() {
        return Optional.of(attributes);
    }

    private Optional<Node> tryCreate0() {
        return Optional.of(new ImmutableNode(name, attributes));
    }

    private Optional<Error_> findError0() {
        return Optional.empty();
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
        return JavaOptionals.fromNative(findError0());
    }

    @Override
    public Option<Attributes> findAttributes() {
        return JavaOptionals.fromNative(findAttributes0());
    }

    @Override
    public Option<Node> tryCreate() {
        return JavaOptionals.fromNative(tryCreate0());
    }

    @Override
    public Result<Node, Error_> create() {
        return tryCreate()
                .<Result<Node, Error_>>map(Ok::new)
                .orElseGet(() -> findError().map(err -> new Err<Node, Error_>(err)).orElseGet(() -> new Err<>(new CompileError("Neither value nor error is present.", ""))));
    }
}
