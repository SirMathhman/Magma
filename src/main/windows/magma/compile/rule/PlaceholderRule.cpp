// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PlaceholderRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct PlaceholderRule {/*Rule*/ rule;};
/*private static String*/ wrap_PlaceholderRule(/*String*/ input) {
	/*return "start" + input.replace*/(/*"start"*/, /* "start").replace("end"*/, /* "end") + "end"*/;
}
/*public static Rule*/ Placeholder_PlaceholderRule(/*Rule*/ rule) {
	/*return new PlaceholderRule*/(/*rule)*/;
}
/*@Override
	public Result<Node, CompileError>*/ lex_PlaceholderRule(/*String*/ content) {
	/*return rule.lex*/(/*content)*/;
}
/*@Override
	public Result<String, CompileError>*/ generate_PlaceholderRule(/*Node*/ node) {
	/*return rule.generate*/(/*node).mapValue(PlaceholderRule::wrap)*/;
}
