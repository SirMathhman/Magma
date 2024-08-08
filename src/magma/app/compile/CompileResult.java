package magma.app.compile;

import java.util.Objects;

public final class CompileResult {
    private final String output;
    private final Node inputNode;
    private final Node outputNode;

    public CompileResult(String output, Node inputNode, Node outputNode) {
        this.output = output;
        this.inputNode = inputNode;
        this.outputNode = outputNode;
    }

    public String output() {
        return output;
    }

    public Node inputNode() {
        return inputNode;
    }

    public Node outputNode() {
        return outputNode;
    }
}
