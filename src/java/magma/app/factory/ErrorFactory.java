package magma.app.factory;

interface ErrorFactory<Context, Error, ErrorList> {
    Error createError(String message, Context context);

    Error createErrorWithChildren(String message, Context context, ErrorList errors);
}
