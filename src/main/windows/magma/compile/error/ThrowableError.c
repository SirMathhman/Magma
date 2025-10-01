struct ThrowableError(Throwable e) implements Error{};
char* display_ThrowableError(Throwable e) implements Error() {/*
		final StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	*/}
