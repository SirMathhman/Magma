package magma.build.compile.parse.result;

import magma.api.option.Option;
import magma.build.compile.error.Error_;
import magma.build.compile.attribute.Attributes;
import magma.build.compile.parse.ImmutableNode;
import magma.build.compile.parse.Node;
import magma.build.java.JavaOptionals;

import java.util.Optional;
import java.util.function.Function;

public record TypedRuleResult(String name, Attributes attributes) implements RuleResult{

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
    public RuleResult withType(String type) {
        return this;
    }

    @Override
    public RuleResult mapErr(Function<Error_, Error_> mapper) {
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
}
