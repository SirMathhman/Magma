package magma.compile.rule.split;

import magma.api.Tuple;
import magma.api.result.Result;
import magma.compile.CompileError;
import magma.compile.CompileParentError;
import magma.compile.Error_;
import magma.compile.rule.Node;
import magma.compile.rule.Rule;
import magma.compile.rule.result.ErrorRuleResult;
import magma.compile.rule.result.RuleResult;
import magma.compile.rule.result.UntypedRuleResult;
import magma.java.JavaOptionals;

public class SplitOnceRule implements Rule {
    protected final Rule leftRule;
    protected final String slice;
    protected final Rule rightRule;
    private final Searcher searcher;

    public SplitOnceRule(Rule leftRule, String slice, Rule rightRule, Searcher searcher) {
        this.leftRule = leftRule;
        this.slice = slice;
        this.rightRule = rightRule;
        this.searcher = searcher;
    }

    public RuleResult toNode(String input) {
        var tuple = searcher.search(input).map(keywordIndex -> {
            var left1 = input.substring(0, keywordIndex);
            var right1 = input.substring(keywordIndex + slice.length());
            return new Tuple<>(left1, right1);
        });

        return tuple.map(contentStart -> {
            var left = contentStart.left();
            var right = contentStart.right();

            var leftResult = leftRule.toNode(left);
            if (JavaOptionals.toNative(leftResult.findError()).isPresent()) return leftResult;

            var rightResult = rightRule.toNode(right);
            if (JavaOptionals.toNative(rightResult.findError()).isPresent()) return rightResult;

            return JavaOptionals.toNative(leftResult.findAttributes())
                    .flatMap(leftAttributes -> JavaOptionals.toNative(rightResult.findAttributes()).map(rightAttributes -> rightAttributes.merge(leftAttributes)))
                    .map(UntypedRuleResult::new)
                    .orElseThrow();
        }).orElseGet(() -> {
            var format = "Slice '%s' not present.";
            var message = format.formatted(slice);
            return new ErrorRuleResult(new CompileError(message, input));
        });
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        var leftResult = leftRule.fromNode(node);
        var rightValue = rightRule.fromNode(node);

        return leftResult
                .flatMapValue(left -> rightValue.mapValue(right -> left + slice + right))
                .mapErr(err -> createError(node, err));
    }

    private CompileParentError createError(Node node, Error_ err) {
        var format = "Cannot merge node using slice '%s'.";
        var message = format.formatted(slice);
        return new CompileParentError(message, node.toString(), err);
    }
}
