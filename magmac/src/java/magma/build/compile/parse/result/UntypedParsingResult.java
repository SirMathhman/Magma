package magma.build.compile.parse.result;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.attribute.Attributes;
import magma.build.compile.parse.Node;

import java.util.function.Function;

public record UntypedParsingResult(Attributes attributes) implements ParsingResult {
    @Override
    public ParsingResult withType(String type) {
        return new TypedParsingResult(type, attributes);
    }

    @Override
    public ParsingResult mapErr(Function<Error_, Error_> mapper) {
        return this;
    }

    @Override
    public Option<Error_> findError() {
        return None.None();
    }

    @Override
    public Option<Attributes> findAttributes() {
        return new Some<>(attributes);
    }

    @Override
    public Option<Node> tryCreate() {
        return None.None();
    }

    @Override
    public Result<Node, Error_> create() {
        return tryCreate()
                .<Result<Node, Error_>>map(Ok::new)
                .orElseGet(() -> findError().map(err -> new Err<Node, Error_>(err)).orElseGet(() -> new Err<>(new CompileError("Neither value nor error is present.", ""))));
    }
}
