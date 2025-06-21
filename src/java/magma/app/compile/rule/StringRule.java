package magma.app.compile.rule;

import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.result.CompileError;
import magma.app.compile.result.GenerateErr;
import magma.app.compile.result.GenerateOk;
import magma.app.compile.result.GenerateResult;
import magma.app.compile.result.LexOk;
import magma.app.compile.result.LexResult;

public record StringRule(String key) implements Rule {
    @Override
    public LexResult lex(final String input) {
        final var node = MapNode.empty()
                .withString(this.key, input);

        return new LexOk(node);
    }

    @Override
    public GenerateResult generate(final Node node) {
        return node.findString(this.key)
                .<GenerateResult>map(GenerateOk::new)
                .orElseGet(() -> new GenerateErr(new CompileError("String '" + this.key + "' not present",
                        node.asString())));
    }
}