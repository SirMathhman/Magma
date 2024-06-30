package magma.build.compile.parse.text.extract;

import magma.build.compile.Error_;

import java.util.Optional;

public final class SimpleExtractStringListRule extends ExtractStringListRule {
    public SimpleExtractStringListRule(String key, String delimiter) {
        super(key, delimiter);
    }

    @Override
    protected Optional<Error_> qualify(String child) {
        return Optional.empty();
    }
}