// Generated transpiled C++ from 'src\main\java\magma\Main.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Main {};
/*???*/ main_Main(/*???*/* args) {
	if (/*???*/)System.err.println(value.display());
}
Option</*???*/> run_Main() {
	/*???*/ javaSourceRoot=Paths.get("", "", "", "");
	/*???*/ cOutputRoot=Paths.get("", "", "", "");/*???*//*???*/
	return compileAllJavaFiles(javaSourceRoot, cOutputRoot);
}
Option</*???*/> compileAllJavaFiles_Main(/*???*/ javaSourceRoot, /*???*/ cOutputRoot) {/*???*//*???*/
}
Option</*???*/> compileAll_Main(/*???*/ javaSourceRoot, /*???*/ cOutputRoot, List</*???*/> javaFiles) {
	/*???*/ i=/*???*/;
	while (/*???*/)
	{
	/*???*/ javaFile=javaFiles.get(i).orElse(null);
	System.out.println(""+javaFile);
	/*???*/(javaFile, javaSourceRoot, cOutputRoot);
	if (/*???*/)
	{
	System.err.println(""+javaFile+""+error.display());
	/*???*/ result;}
	System.out.println(""+javaFile);
	i++;}
	return Option.empty();
}
Option</*???*/> compileJavaFile_Main(/*???*/ javaFile, /*???*/ javaSourceRoot, /*???*/ cOutputRoot) {
	/*???*/ relativePath=javaSourceRoot.relativize(javaFile);
	/*???*/ fileName=relativePath.getFileName().toString();
	/*???*/ cFileName=fileName.substring(/*???*/, fileName.lastIndexOf('.'))+"";
	/*???*/ cFilePath=cOutputRoot.resolve(relativePath.getParent()).resolve(cFileName);/*???*//*???*/
	Result</*???*/, /*???*/> readResult=readString(javaFile);
	if (/*???*/)return Option.of(new_???(error));
	if (/*???*/)return Option.empty();
	Result</*???*/, /*???*/> compileResult=Compiler.compile(input);
	if (/*???*/)return Option.of(new_???(error));
	if (/*???*/)
	{
	/*???*/ message=formatMessage(javaFile);
	return writeString(cFilePath, message+compiled).map(/*???*/).map(/*???*/);}
	return Option.empty();
}
/*???*/ formatMessage_Main(/*???*/ javaFile) {
	/*???*/ relative=Paths.get("").relativize(javaFile);
	return ""+relative+""+System.lineSeparator();
}
Option</*???*/> writeString_Main(/*???*/ path, /*???*/ result) {/*???*//*???*/
}
Result</*???*/, /*???*/> readString_Main(/*???*/ source) {/*???*//*???*/
}
