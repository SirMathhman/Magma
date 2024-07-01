package magma.build.compile.parse.result;

import magma.api.option.Option;
import magma.api.result.Result;
import magma.build.compile.attribute.Attributes;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;

import java.util.function.Function;

public interface ParsingResult {
    Option<Error_> findError();

    Option<Attributes> findAttributes();

    Option<Node> tryCreate();

    ParsingResult withType(String type);

    ParsingResult mapErr(Function<Error_, Error_> mapper);

    Result<Node, Error_> create();
}
