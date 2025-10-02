// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SuffixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SuffixRule {/*Rule*/ rule;/* String*/ suffix;};
/*public static Rule*/ Suffix_SuffixRule(/*Rule*/ rule, /* String*/ suffix) {
	/*return new SuffixRule*/(/*rule*/, /* suffix)*/;
}
/*@Override
	public Result<Node, CompileError>*/ lex_SuffixRule(/*String*/ input) {
	/*if */(/*!input.endsWith(suffix()))
			return new Err<>(new CompileError("Suffix '" + suffix + "' not present"*/, /* new StringContext(input)))*/;
	/*final String slice */=/* input.substring(0, input.length() - suffix().length())*/;
	/*return getRule*/(/*).lex(slice)*/;
}
/*@Override
	public Result<String, CompileError>*/ generate_SuffixRule(/*Node*/ node) {
	/*return rule.generate*/(/*node).mapValue(value -> value + suffix())*/;
}
/*public Rule*/ getRule_SuffixRule() {
	return /*rule*/;
}
