// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StripRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StripRule {/*String*/ leftKey;/* Rule*/ rule;/* String*/ rightKey;};
/*public static Rule*/ Strip_StripRule(/*Rule*/ rule) {
	/*return new StripRule*/(/*"?"*/, /* rule*/, /* "?")*/;
}
/*public static Rule*/ Strip_StripRule(/*String*/ left, /* Rule*/ rule, /* String*/ right) {
	/*return new StripRule*/(/*left*/, /* rule*/, /* right)*/;
}
/*@Override
	public Result<Node, CompileError>*/ lex_StripRule(/*String*/ content) {
	/*return rule.lex*/(/*content.strip())*/;
}
/*@Override
	public Result<String, CompileError>*/ generate_StripRule(/*Node*/ node) {
	/*return rule.generate(node).mapValue(generated -> {
			final String leftString */=/* node.findString(leftKey).orElse("");
			final String rightString = node.findString(rightKey).orElse(""); return leftString + generated + rightString;
		})*/;
}
