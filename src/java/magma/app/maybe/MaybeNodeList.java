package magma.app.maybe;

import magma.app.Node;

import java.util.List;
import java.util.function.Function;

public interface MaybeNodeList {
    MaybeNodeList add(MaybeNode<MaybeNodeList> node);

    MaybeNodeList transform(Function<List<Node>, List<Node>> mapper);

    MaybeString generate(Function<List<Node>, MaybeString> generator);
}
