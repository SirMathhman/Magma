package magma.app.compile.rule.result;

import magma.api.result.Result;
import magma.app.compile.CompileError;

import java.util.Optional;
import java.util.function.Function;

public interface RuleResult<Node> {
    <Return> RuleResult<Return> flatMap(Function<Node, RuleResult<Return>> mapper);

    <Return> RuleResult<Return> mapValue(Function<Node, Return> mapper);

    Optional<Node> findAsOption();

    Result<Node, CompileError> unwrap();
}