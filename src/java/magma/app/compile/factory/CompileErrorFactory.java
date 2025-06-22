package magma.app.compile.factory;

import magma.api.error.list.ErrorSequence;
import magma.app.compile.context.Context;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.FormattedError;

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