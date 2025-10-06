// Generated transpiled C++ from 'src\main\java\magma\Main.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Main {};
void main_Main(char** args) {
	if (/*???*/)System.err.println(value.display());
}
Option<> run_Main() {
	Path javaSourceRoot=Paths.get("", "", "", "");
	Path cOutputRoot=Paths.get("", "", "", "");/*???*//*???*/
	return compileAllJavaFiles(javaSourceRoot, cOutputRoot);
}
Option<> compileAllJavaFiles_Main(Path javaSourceRoot, Path cOutputRoot) {/*???*//*???*/
}
Option<> compileAll_Main(Path javaSourceRoot, Path cOutputRoot, List<> javaFiles) {
	int i=/*???*/;
	while (/*???*/)
	{
	Path javaFile=javaFiles.get(i);
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
Option<> compileJavaFile_Main(Path javaFile, Path javaSourceRoot, Path cOutputRoot) {
	Path relativePath=javaSourceRoot.relativize(javaFile);
	char* fileName=relativePath.getFileName().toString();
	char* cFileName=fileName.substring(/*???*/, fileName.lastIndexOf('.'))+"";
	Path cFilePath=cOutputRoot.resolve(relativePath.getParent()).resolve(cFileName);/*???*//*???*/
	Result<> readResult=readString(javaFile);
	if (/*???*/)return Option.of(new_???(error));
	if (/*???*/)return Option.empty();
	Result<> compileResult=Compiler.compile(input);
	if (/*???*/)return Option.of(new_???(error));
	if (/*???*/)
	{
	char* message=formatMessage(javaFile);
	return writeString(cFilePath, message+compiled).map(/*???*/).map(/*???*/);}
	return Option.empty();
}
char* formatMessage_Main(Path javaFile) {
	Path relative=Paths.get("").relativize(javaFile);
	return ""+relative+""+System.lineSeparator();
}
Option<> writeString_Main(Path path, char* result) {/*???*//*???*/
}
Result<> readString_Main(Path source) {/*???*//*???*/
}
