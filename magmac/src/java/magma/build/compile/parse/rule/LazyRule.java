package magma.build.compile.parse.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;

import java.util.Optional;

public class LazyRule implements Rule {
    private Optional<Rule> child;

    public LazyRule() {
        this.child = Optional.empty();
    }

    public void setRule(Rule child) {
        this.child = Optional.of(child);
    }

    private ParsingResult toNode0(String input) {
        return child
                .map(inner -> Rules.toNode(inner, input))
                .orElse(new ErrorParsingResult(new CompileError("Child was not set.", input)));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.map(inner -> inner.fromNode(node))
                .orElse(new Err<>(new CompileError("No child set.", node.toString())));
    }

    @Override
    public ParsingResult toNode(String input) {
        return toNode0(input);
    }
}
