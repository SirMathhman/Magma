package magma.app.compile;

import magma.api.Tuple;
import magma.api.map.MapLike;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;
import magma.app.compile.transform.Transformer;

import java.util.Optional;
import java.util.stream.Collectors;

public class StageCompiler implements Compiler {
    private final Rule<NodeWithEverything, NodeResult<NodeWithEverything>, StringResult> sourceRule;
    private final Rule<NodeWithEverything, NodeResult<NodeWithEverything>, StringResult> targetRule;
    private final Transformer transformer;

    public StageCompiler(Rule<NodeWithEverything, NodeResult<NodeWithEverything>, StringResult> sourceRule, Transformer transformer, Rule<NodeWithEverything, NodeResult<NodeWithEverything>, StringResult> targetRule) {
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
                .findValue()
                .map(root -> this.transformer.transform(root, name))
                .flatMap(node -> this.targetRule.generate(node)
                        .findValue());
    }
}
