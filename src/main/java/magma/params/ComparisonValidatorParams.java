package magma.params;

import magma.core.ValueProcessor;
import magma.validation.ArithmeticValidator;

import java.util.Map;

/**
 * Parameter object for ComparisonValidator constructor.
 * Groups related parameters to reduce parameter count.
 */
public record ComparisonValidatorParams(
        ValueProcessor valueProcessor,
        Map<String, String> variableTypes,
        ArithmeticValidator arithmeticValidator
) {
}
