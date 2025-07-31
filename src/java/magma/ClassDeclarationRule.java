package magma;

import magma.node.Node;

import java.util.Optional;

public final class ClassDeclarationRule implements Rule {
    private final Rule nameRule;

    public ClassDeclarationRule(final Rule nameRule) {
        this.nameRule = nameRule;
    }

    @Override
    public Optional<String> generate(final Node node) {
        return this.nameRule.generate(node)
                .map(name -> "export class " + name + " {}");
    }
}