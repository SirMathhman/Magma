package magma.build.compile.parse.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.error.MultipleError;
import magma.build.compile.error.TimeoutError;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;
import magma.build.java.JavaOptionals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record OrRule(List<Rule> rules) implements Rule {
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

        return anyOk.orElseGet(() -> new Err<>(new MultipleError(results.stream()
                .map(stringErrorResult -> JavaOptionals.toNative(stringErrorResult.findErr()))
                .flatMap(Optional::stream)
                .toList())));

    }

    @Override
    public ParsingResult toNode(String input) {
        var errors = new ArrayList<Error_>();
        var totalTime = Duration.ZERO;

        for (Rule rule : this.rules()) {
            var result = Rules.toNode(rule, input);
            var durationOption = result.findDuration();
            if (durationOption.isEmpty()) {
                return new ErrorParsingResult(new CompileError("No duration present.", input));
            }

            totalTime = totalTime.plus(durationOption.orElsePanic());
            if(Rules.exceedsTimeout(totalTime)) {
                return new ErrorParsingResult(new TimeoutError(input, totalTime));
            }

            if (result.findAttributes().isPresent()) {
                return result;
            }

            var errorOption = result.findError();
            if (errorOption.isPresent()) {
                errors.add(errorOption.orElsePanic());
            }
        }

        if (errors.isEmpty()) {
            return new ErrorParsingResult(new CompileError("No rules were present.", input));
        } else {
            return new ErrorParsingResult(new MultipleError(errors));
        }
    }
}