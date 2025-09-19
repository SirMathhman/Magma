package magma;

public class ExpressionTypes {
    public static class Reduction {
        public final java.util.List<Long> values;
        public final java.util.List<Character> ops;

        public Reduction(java.util.List<Long> values, java.util.List<Character> ops) {
            this.values = values;
            this.ops = ops;
        }
    }

    public static class ExpressionTokens {
        public final java.util.List<OperandParseResult> operands = new java.util.ArrayList<>();
        public final java.util.List<Character> operators = new java.util.ArrayList<>();
    }

    public static class OperandParseResult {
        public final long value;
        public final String suffix;
        public final int nextPos;

        public OperandParseResult(long value, String suffix, int nextPos) {
            this.value = value;
            this.suffix = suffix;
            this.nextPos = nextPos;
        }
    }
}
