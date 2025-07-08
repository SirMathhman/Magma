/*import magma.Node;*/
/*import java.util.Optional;*/
/*public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule */{/*
    @Override
    public Optional<Node> lex(final String input) {
        final var index = input.indexOf(this.infix);
        if (0 > index) return Optional.empty();

        final var leftString = input.substring(0, index).strip();
        final var leftResult = this.leftRule.lex(leftString);
        return leftResult.flatMap(left -> {
            final var rightString = input.substring(index + this.infix.length());
            final var rightResult = this.rightRule.lex(rightString);
            return rightResult.map(left::merge);
        });
    }

    @Override
    public Optional<String> generate(final Node node) {
        return this.leftRule.generate(node)
                            .flatMap(leftResult -> this.rightRule.generate(node)
                                                                 .map(rightResult -> leftResult + this.infix +
                                                                                     rightResult));
    }
*/}
