package magma.app.compile.error;

import magma.api.Err;
import magma.api.Ok;
import magma.app.compile.context.Context;
import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.node.NodeWithEverything;

import java.util.ArrayList;
import java.util.List;

public class ResultCompileResultFactory implements CompileResultFactory<NodeWithEverything> {
    private ResultCompileResultFactory() {
    }

    public static CompileResultFactory<NodeWithEverything> createResultCompileResultFactory() {
        return new ResultCompileResultFactory();
    }

    @Override
    public <Value> CompileResult<Value> fromValue(Value input) {
        return new ResultCompileResult<>(new Ok<>(input));
    }

    @Override
    public <Value> CompileResult<Value> fromStringError0(String message, String input) {
        return new ResultCompileResult<>(new Err<>(new CompileError(message, new StringContext(input))));
    }

    @Override
    public CompileResult<NodeWithEverything> fromNode(NodeWithEverything node) {
        return this.fromValue(node);
    }

    @Override
    public CompileResult<String> fromString(String generated) {
        return this.fromValue(generated);
    }

    @Override
    public CompileResult<String> fromNodeError(String message, NodeWithEverything context) {
        return this.withContext(message, new NodeContext(context));
    }

    @Override
    public CompileResult<NodeWithEverything> fromStringError(String message, String context) {
        return this.withContext(message, new StringContext(context));
    }

    private <Node> CompileResult<Node> withContext(String message, Context context) {
        return new ResultCompileResult<>(new Err<>(new CompileError(message, context)));
    }

    @Override
    public CompileResult<List<NodeWithEverything>> fromEmptyNodeList() {
        return this.fromValue(new ArrayList<>());
    }

    @Override
    public CompileResult<String> fromEmptyString() {
        return this.fromString("");
    }
}
