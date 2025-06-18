package magma.app;

import magma.api.Tuple;
import magma.api.map.MapLike;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;
import magma.app.compile.transform.Transformer;

import java.util.Optional;
import java.util.stream.Collectors;

public class StageCompiler implements Compiler {
    private final Rule<NodeWithEverything> sourceRule;
    private final Rule<NodeWithEverything> targetRule;
    private final Transformer transformer;

    public StageCompiler(Rule<NodeWithEverything> sourceRule, Transformer transformer, Rule<NodeWithEverything> targetRule) {
        this.sourceRule = sourceRule;
        this.targetRule = targetRule;
        this.transformer = transformer;
    }

    @Override
    public String compile(MapLike<String, String> sourceMap) {
        return sourceMap.stream()
                .map(this::compileSourceMapEntry)
                .flatMap(Optional::stream)
                .collect(Collectors.joining());
    }

    private Optional<String> compileSourceMapEntry(Tuple<String, String> entry) {
        final var name = entry.left();
        final var input = entry.right();
        return this.sourceRule.lex(input)
                .map(root -> this.transformer.transform(root, name))
                .flatMap(this.targetRule::generate);
    }
}
