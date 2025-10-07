// Generated transpiled C++ from 'src\main\java\magma\Main.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Main {};
void main_Main() {
	if (/*???*/)System.err.println();
}
Option<ApplicationError> run_Main() {
	Path javaSourceRoot=Paths.get();
	Path cOutputRoot=Paths.get();/*???*//*???*/
	return compileAllJavaFiles();
}
Option<ApplicationError> compileAllJavaFiles_Main() {/*???*//*???*/
}
Option<ApplicationError> compileAll_Main() {
	int i=/*???*/;
	while (/*???*/)
	{
	Path javaFile=javaFiles.get().orElse();
	System.out.println();
	/*???*/();
	if (/*???*/)
	{
	System.err.println();
	return result;}
	System.out.println();
	i++;}
	return Option.empty();
}
Option<ApplicationError> compileJavaFile_Main() {
	Path relativePath=javaSourceRoot.relativize();
	String fileName=relativePath.getFileName().toString();
	String cFileName=fileName.substring()+"";
	Path cFilePath=cOutputRoot.resolve().resolve();/*???*//*???*/
	Result<String, ThrowableError> readResult=readString();
	if (/*???*/)return Option.of();
	if (/*???*/)return Option.empty();
	Result<String, CompileError> compileResult=Compiler.compile();
	if (/*???*/)return Option.of();
	if (/*???*/)
	{
	String message=formatMessage();
	return writeString().map().map();}
	return Option.empty();
}
String formatMessage_Main() {
	Path relative=Paths.get().relativize();
	return ""+relative+""+System.lineSeparator();
}
Option<IOException> writeString_Main() {/*???*//*???*/
}
Result<String, ThrowableError> readString_Main() {/*???*//*???*/
}
