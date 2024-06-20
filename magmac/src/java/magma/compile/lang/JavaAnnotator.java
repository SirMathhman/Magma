package magma.compile.lang;

import magma.api.Tuple;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.compile.CompileError;
import magma.compile.Error_;
import magma.compile.rule.Node;

import java.util.Collections;
import java.util.List;

public class JavaAnnotator extends Generator {
    @Override
    protected Result<Tuple<Node, State>, Error_> preVisit(Node node, State state) {
        if (node.is("block")) {
            return new Ok<>(new Tuple<>(node, state.enter()));
        }



        if (node.is("method") || node.is("record")) {
            var defined = defineParams(state, node.findNodeList("params")
                    .orElse(Collections.emptyList()));

            return defined.mapValue(newState -> new Tuple<>(node, newState));
        }

        return new Ok<>(new Tuple<>(node, state));
    }

    private static Result<State, Error_> defineParams(State state, List<Node> params) {
        Result<State, Error_> defined = new Ok<>(state);
        for (Node param : params) {
            var name = param.findString("name").orElseThrow();
            defined = defined.flatMapValue(inner -> inner.define(name));
        }
        return defined;
    }

    @Override
    protected Result<Tuple<Node, State>, Error_> postVisit(Node node, State state) {
        if (node.is("block")) {
            return new Ok<>(new Tuple<>(node, state.exit()));
        }


        if (node.is("symbol")) {
            var value = node.findString("value").orElseThrow();
            if (!state.isDefined(value)) {
                return new Err<>(new CompileError("Symbol not defined.", value));
            }
        }

        return new Ok<>(new Tuple<>(node, state));
    }
}
