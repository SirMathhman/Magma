// Generated transpiled C++ from 'src\main\java\magma\compile\rule\OptionalNodeListRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct OptionalNodeListRule {};
public OptionalNodeListRule_OptionalNodeListRule() {
	this.key=key;
	this.ifPresent=ifPresent;
	this.ifEmpty=ifEmpty;
}
Result<Node, CompileError> lex_OptionalNodeListRule() {
	lexRule=new_???();
	return lexRule.lex();
}
Result<String, CompileError> generate_OptionalNodeListRule() {
	if (node.hasNodeList())
	{
	return ifPresent.generate();}
	else
	return ifEmpty.generate();
}
