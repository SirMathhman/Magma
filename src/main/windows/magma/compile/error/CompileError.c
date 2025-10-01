struct CompileError(String reason, Context context, List<CompileError> causes) implements Error{};
public CompileError_CompileError(String reason, Context context, List<CompileError> causes) implements Error() {}
String display_CompileError(String reason, Context context, List<CompileError> causes) implements Error() {}
String format_CompileError(String reason, Context context, List<CompileError> causes) implements Error() {}
int depth_CompileError(String reason, Context context, List<CompileError> causes) implements Error() {}
