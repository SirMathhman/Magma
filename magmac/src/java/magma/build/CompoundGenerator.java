package magma.build;

import magma.api.Tuple;
import magma.api.contain.stream.Streams;
import magma.api.result.Result;
import magma.build.compile.Error_;
import magma.build.compile.annotate.State;
import magma.build.compile.lang.Generator;
import magma.build.compile.rule.Node;

import java.util.List;

public record CompoundGenerator(List<Generator> children) implements Generator {
    @Override
    public Result<Tuple<Node, State>, Error_> generate(Node root, State state) {
        var initial = new Tuple<>(root, state);
        return Streams.fromNativeList(children()).foldLeftToResult(initial,
                (tuple, generator) -> generator.generate(tuple.left(), tuple.right()));
    }
}