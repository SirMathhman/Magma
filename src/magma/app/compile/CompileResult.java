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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CompileResult) obj;
        return Objects.equals(this.output, that.output) &&
               Objects.equals(this.inputNode, that.inputNode) &&
               Objects.equals(this.outputNode, that.outputNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(output, inputNode, outputNode);
    }

    @Override
    public String toString() {
        return "CompileResult[" +
               "output=" + output + ", " +
               "inputNode=" + inputNode + ", " +
               "outputNode=" + outputNode + ']';
    }

}
