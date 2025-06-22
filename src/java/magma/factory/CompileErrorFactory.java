package magma.factory;

import magma.api.error.ErrorSequence;
import magma.api.error.FormattedError;
import magma.error.CompileError;
import magma.error.Context;

public class CompileErrorFactory implements ErrorFactory<Context, FormattedError, ErrorSequence<FormattedError>> {
    @Override
    public FormattedError createError(final String message, final Context context) {
        return new CompileError(message, context);
    }

    @Override
    public FormattedError createErrorWithChildren(final String message, final Context context, final ErrorSequence<FormattedError> errors) {
        return new CompileError(message, context, errors);
    }
}