struct CompileError(String reason, Context context, List<CompileError> causes) implements Error {};
public CompileError
String display
String format
int depth
