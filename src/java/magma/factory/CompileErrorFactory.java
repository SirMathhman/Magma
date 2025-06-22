package magma.factory;

import magma.error.CompileError;
import magma.error.Context;
import magma.error.ErrorList;
import magma.error.FormattedError;

public class CompileErrorFactory implements ErrorFactory<Context, FormattedError, ErrorList<FormattedError>> {
    @Override
    public FormattedError createError(final String message, final Context context) {
        return new CompileError(message, context);
    }

    @Override
    public FormattedError createErrorWithChildren(final String message, final Context context, final ErrorList<FormattedError> errors) {
        return new CompileError(message, context, errors);
    }
}