// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PrefixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct PrefixRule {/*String*/ prefix;/* Rule*/ rule;};
/*public static Rule*/ Prefix_PrefixRule(/*String*/ prefix, /* Rule*/ rule) {
	/*return new PrefixRule*/(/*prefix*/, /* rule)*/;
}
/*@Override
	public Result<Node, CompileError>*/ lex_PrefixRule(/*String*/ content) {
	/*if */(/*content.startsWith(prefix)) return rule.lex(content.substring(prefix.length()))*/;
	/*else return new Err<>*/(/*new CompileError("Prefix '" + prefix + "' not present"*/, /* new StringContext(content)))*/;
}
/*@Override
	public Result<String, CompileError>*/ generate_PrefixRule(/*Node*/ node) {
	/*return rule.generate*/(/*node).mapValue(inner -> prefix + inner)*/;
}
