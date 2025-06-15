package magma.app;

import magma.Main;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeString;

import java.util.List;
import java.util.function.Function;

public interface MaybeNodeList {
    MaybeNodeList add(MaybeNode node);

    MaybeNodeList transform(Function<List<Node>, List<Node>> mapper);

    MaybeString generate(Function<List<Node>, MaybeString> generator);
}
