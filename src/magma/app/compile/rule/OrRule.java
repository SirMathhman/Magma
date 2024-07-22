package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.CompileException;

import java.util.List;

public record OrRule(List<Rule> children) implements Rule {

    @Override
    public Result<Node, CompileException> parse(String input) {
        for (Rule child : children) {
            var result = child.parse(input);
            if (result.isOk()) return result;
        }

        return new Err<>(new CompileException("No rules present", input));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        for (Rule child : children) {
            var result = child.generate(node);
            if (result.isOk()) return result;
        }

        return new Err<>(new CompileException("No rules present", node.toString()));
    }
}
