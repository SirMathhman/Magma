package magma.build.compile.parse.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.error.MultipleError;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;
import magma.build.java.JavaOptionals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record OrRule(List<Rule> rules) implements Rule {
    private static Err<String, Error_> toError(List<Result<String, Error_>> results) {
        return new Err<>(new MultipleError(results.stream()
                .map(stringErrorResult -> JavaOptionals.toNative(stringErrorResult.findErr()))
                .flatMap(Optional::stream)
                .toList()));
    }

    private ParsingResult toNode0(String input) {
        var errors = new ArrayList<Error_>();
        for (Rule rule : rules()) {
            var result = Rules.toNode(rule, input);
            if (JavaOptionals.toNative(result.findAttributes()).isPresent()) {
                return result;
            }

            JavaOptionals.toNative(result.findError()).ifPresent(errors::add);
        }

        if (errors.isEmpty()) {
            return new ErrorParsingResult(new CompileError("No rules were present.", input));
        } else {
            return new ErrorParsingResult(new MultipleError(errors));
        }
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        var results = new ArrayList<Result<String, Error_>>();

        for (var rule : rules) {
            var result = rule.fromNode(node);
            results.add(result);
        }

        Optional<Result<String, Error_>> anyOk = Optional.empty();
        for (Result<String, Error_> result : results) {
            if (result.isOk()) {
                anyOk = Optional.of(result);
                break;
            }
        }

        return anyOk.orElseGet(() -> toError(results));

    }

    @Override
    public ParsingResult toNode(String input) {
        return toNode0(input);
    }
}