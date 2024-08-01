package magma.app.compile.rule;

import magma.api.Result;

import java.util.Collections;
import java.util.List;

public record RuleResult<T, E extends Exception>(Result<T, E> result, List<RuleResult<T, E>> children) {
    public RuleResult(Result<T, E> result) {
        this(result, Collections.emptyList());
    }

    public boolean isValid() {
        return result.isOk();
    }
}
