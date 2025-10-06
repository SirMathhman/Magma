// Generated transpiled C++ from 'src\main\java\magma\compile\error\ThrowableError.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ThrowableError {Throwable e;};
char* display_ThrowableError() {
	StringWriter writer=new_???();
	e.printStackTrace(new_???(writer));
	return writer.toString();
}
