package magma.build.compile.parse.rule;

import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;

public class FilterRule implements Rule {
    protected final Rule child;
    private final Filter filter;

    public FilterRule(Rule child, Filter filter) {
        this.child = child;
        this.filter = filter;
    }

    @Override
    public ParsingResult toNode(String input) {
        if (filter.filter(input)) return child.toNode(input);
        return new ErrorParsingResult(new CompileError("Invalid filter: " + filter.computeMessage(), input));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node);
    }

}
