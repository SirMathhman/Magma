package magma.build.compile.lang.java;

import magma.api.Tuple;
import magma.api.contain.List;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.error.Error_;
import magma.build.compile.annotate.State;
import magma.build.compile.lang.Visitor;
import magma.build.compile.parse.Node;
import magma.build.java.JavaList;

public class InterfaceNormalizer implements Visitor {
    @Override
    public Result<Tuple<Node, State>, Error_> preVisit(Node node, State state) {
        var struct = node.retype("struct").mapStringList("modifiers", InterfaceNormalizer::normalizeModifiers);

        return new Ok<>(new Tuple<>(struct, state));
    }

    private static List<String> normalizeModifiers(List<String> oldModifiers) {
        return oldModifiers.contains("public")
                ? JavaList.of("export")
                : JavaList.empty();
    }
}
