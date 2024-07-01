package magma.build.compile.parse.rule.split;

import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.CompileParentError;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;
import magma.build.compile.parse.result.UntypedParsingResult;
import magma.build.compile.error.Error_;
import magma.build.compile.error.MultipleError;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.rule.Rule;
import magma.build.compile.parse.rule.Rules;
import magma.build.java.JavaOptionals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record BackwardsRule(Rule leftRule, String slice, Rule rightRule) implements Rule {
    private ParsingResult toNode0(String input) {
        var allIndexes = findAllIndexesReverse(input);
        ParsingResult current = new ErrorParsingResult(new MultipleError());

        for (var index : allIndexes) {
            var leftSlice = input.substring(0, index);
            var rightSlice = input.substring(index + slice.length());

            var leftResult = Rules.toNode(leftRule, leftSlice);
            current = current.merge(() -> leftResult.merge(() -> Rules.toNode(rightRule, rightSlice)));
        }

        return current;
    }

    private static CompileParentError wrapError(String leftSlice, String rightSlice, Error_ error) {
        return new CompileParentError("Invalid combination.", "[\"" + leftSlice + "\", \"" + rightSlice + "\"]", error);
    }

    private List<Integer> findAllIndexesReverse(String input) {
        List<Integer> indexes = new ArrayList<>();
        int index = input.indexOf(slice);
        while (index >= 0) {
            indexes.add(index);
            index = input.indexOf(slice, index + 1);
        }
        Collections.reverse(indexes);
        return indexes;
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return leftRule.fromNode(node).flatMapValue(leftResult -> rightRule.fromNode(node).mapValue(rightResult -> leftResult + slice + rightResult));
    }

    @Override
    public ParsingResult toNode(String input) {
        return toNode0(input);
    }
}
