// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FilterRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct FilterRule {/*private final Filter*/ filter;/*private final Rule*/ rule;};
/*public*/ FilterRule_FilterRule(/*Filter*/ filter, /* Rule*/ rule) {
	/*this.filter */=/* filter*/;
	/*this.rule */=/* rule*/;
}
/*public static Rule*/ Filter_FilterRule(/*Filter*/ filter, /* Rule*/ rule) {
	/*return new FilterRule*/(/*filter*/, /* rule)*/;
}
/*public static Rule*/ Identifier_FilterRule(/*Rule*/ rule) {
	/*return Filter*/(/*IdentifierFilter.Identifier*/, /* rule)*/;
}
/*@Override
	public Result<Node, CompileError>*/ lex_FilterRule(/*String*/ content) {
	/*if */(/*filter.test(content)) return rule.lex(content)*/;
	/*return new Err<>*/(/*new CompileError(filter.createErrorMessage()*/, /* new StringContext(content)))*/;
}
/*@Override
	public Result<String, CompileError>*/ generate_FilterRule(/*Node*/ node) {
	/*return rule.generate*/(/*node)*/;
}
