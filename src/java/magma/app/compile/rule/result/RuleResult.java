package magma.app.compile.rule.result;

import magma.api.result.Result;
import magma.app.compile.CompileError;

import java.util.Optional;
import java.util.function.Function;

public interface RuleResult<Node> {
    RuleResult<Node> flatMap(Function<Node, RuleResult<Node>> mapper);

    RuleResult<Node> map(Function<Node, Node> mapper);

    Optional<Node> findAsOption();

    Result<Node, CompileError> findAsResult();
}