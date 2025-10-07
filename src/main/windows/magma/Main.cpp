// Generated transpiled C++ from 'src\main\java\magma\Main.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Main {};
void main_Main(String* args) {
	if (/*???*/)System.err.println(value.display());
}
Option<ApplicationError> run_Main() {
	Path javaSourceRoot=Paths.get("", "", "", "");
	Path cOutputRoot=Paths.get("", "", "", "");/*???*//*???*/
	return compileAllJavaFiles(javaSourceRoot, cOutputRoot);
}
Option<ApplicationError> compileAllJavaFiles_Main(Path javaSourceRoot, Path cOutputRoot) {/*???*//*???*/
}
Option<ApplicationError> compileAll_Main(Path javaSourceRoot, Path cOutputRoot, List<Path> javaFiles) {
	int i=/*???*/;
	while (/*???*/)
	{
	Path javaFile=javaFiles.get(i).orElse(null);
	System.out.println(""+javaFile);
	/*???*/(javaFile, javaSourceRoot, cOutputRoot);
	if (/*???*/)
	{
	System.err.println(""+javaFile+""+error.display());
	return result;}
	System.out.println(""+javaFile);
	i++;}
	return Option.empty();
}
Option<ApplicationError> compileJavaFile_Main(Path javaFile, Path javaSourceRoot, Path cOutputRoot) {
	Path relativePath=javaSourceRoot.relativize(javaFile);
	String fileName=relativePath.getFileName().toString();
	String cFileName=fileName.substring(/*???*/, fileName.lastIndexOf('.'))+"";
	Path cFilePath=cOutputRoot.resolve(relativePath.getParent()).resolve(cFileName);/*???*//*???*/
	Result<String, ThrowableError> readResult=readString(javaFile);
	if (/*???*/)return Option.of(new_???(error));
	if (/*???*/)return Option.empty();
	Result<String, CompileError> compileResult=Compiler.compile(input);
	if (/*???*/)return Option.of(new_???(error));
	if (/*???*/)
	{
	String message=formatMessage(javaFile);
	return writeString(cFilePath, message+compiled).map(/*???*/).map(/*???*/);}
	return Option.empty();
}
String formatMessage_Main(Path javaFile) {
	Path relative=Paths.get("").relativize(javaFile);
	return ""+relative+""+System.lineSeparator();
}
Option<IOException> writeString_Main(Path path, String result) {/*???*//*???*/
}
Result<String, ThrowableError> readString_Main(Path source) {/*???*//*???*/
}
