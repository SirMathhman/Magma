package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Result;
import magma.app.compile.CompileError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class RuleResult<T, E extends CompileError> {
    private final Result<T, E> result;
    private final List<RuleResult<T, E>> children;

    public RuleResult(Result<T, E> result, List<RuleResult<T, E>> children) {
        this.result = result;
        this.children = children;
    }

    private RuleResult(Result<T, E> result) {
        this(result, Collections.emptyList());
    }

    public static <T, E extends CompileError> RuleResult<T, E> RuleResult(Result<T, E> result) {
        return new RuleResult<T, E>(result);
    }

    public int depth() {
        return 1 + children.stream()
                .mapToInt(RuleResult::depth)
                .max()
                .orElse(0);
    }

    public boolean isValid() {
        return result.isOk();
    }

    public Result<T, E> asResult() {
        return result;
    }

    public Optional<T> findValue() {
        return result.findValue();
    }

    public RuleResult<T, E> wrapValue(Function<T, T> mapper) {
        return new RuleResult<>(result.mapValue(mapper), children);
    }

    public RuleResult<T, E> wrapErr(Supplier<E> mapper) {
        if (isValid()) return this;
        return new RuleResult<>(Err.Err(mapper.get()), Collections.singletonList(this));
    }

    public String format(int depth) {
        var indent = "\t".repeat(depth) + depth + ") ";
        return result.match(value -> {
            var joined = formatChildren(depth);
            return indent + value.toString() + "\n" + joined;
        }, err -> {
            var joined = formatChildren(depth);
            var formatted = children.size() != 1 ? err.format() : err.formatWithoutContext();
            return indent + formatted + "\n" + joined;
        });
    }

    private String formatChildren(int depth) {
        return children.stream()
                .sorted(Comparator.comparingInt(RuleResult::depth))
                .map(teRuleResult -> teRuleResult.format(depth + 1))
                .collect(Collectors.joining());
    }

    public RuleResult<T, E> and(Supplier<RuleResult<T, E>> other, BiFunction<T, T, T> merger) {
        var teRuleResult = other.get();
        var newResult = result
                .and(teRuleResult::asResult)
                .mapValue(value -> merger.apply(value.left(), value.right()));

        var wrappedChildren = new ArrayList<>(children);
        wrappedChildren.addAll(teRuleResult.findChildren());

        return new RuleResult<>(newResult, wrappedChildren);
    }

    private Collection<RuleResult<T, E>> findChildren() {
        return children;
    }

    public Result<T, E> result() {
        return result;
    }

    public List<RuleResult<T, E>> children() {
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RuleResult) obj;
        return Objects.equals(this.result, that.result) &&
               Objects.equals(this.children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, children);
    }

    @Override
    public String toString() {
        return "RuleResult[" +
               "result=" + result + ", " +
               "children=" + children + ']';
    }

}
