package magma.app.rule.result;

import java.util.function.Supplier;

public interface MergingLexResult<N> extends LexResult<N, MergingLexResult<N>> {
    MergingLexResult<N> merge(Supplier<LexResult<N, MergingLexResult<N>>> other);
}