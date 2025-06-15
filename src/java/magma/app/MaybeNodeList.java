package magma.app;

import magma.app.maybe.MaybeNode;

import java.util.List;
import java.util.function.Function;

public interface MaybeNodeList {
    MaybeNodeList add(MaybeNode node);

    MaybeNodeList transform(Function<List<Node>, List<Node>> mapper);

    <Return> Return generate(Function<List<Node>, Return> generator);
}
