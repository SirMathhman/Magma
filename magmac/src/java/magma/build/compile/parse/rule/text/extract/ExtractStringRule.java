package magma.build.compile.parse.rule.text.extract;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.error.Error_;
import magma.build.compile.attribute.Attribute;
import magma.build.compile.attribute.StringAttribute;
import magma.build.compile.parse.Node;
import magma.build.java.JavaOptionals;

import java.util.Optional;

public final class ExtractStringRule extends ExtractRule {
    public ExtractStringRule(String key) {
        super(key);
    }

    @Override
    protected Optional<String> fromAttribute(Node attribute) {
        return JavaOptionals.toNative(attribute.findString(key));
    }

    @Override
    protected Result<Attribute, Error_> toAttribute(String content) {
        return new Ok<>(new StringAttribute(content));
    }
}