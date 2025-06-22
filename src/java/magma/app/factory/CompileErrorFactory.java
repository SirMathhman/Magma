package magma.app.factory;

import magma.api.error.list.ErrorSequence;
import magma.app.context.Context;
import magma.app.error.CompileError;
import magma.app.error.FormattedError;

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