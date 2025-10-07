// Generated transpiled C++ from 'src\main\java\magma\compile\CRules.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CRules {};
Rule CRoot_CRules() {
	return Statements();
}
Rule CFunction_CRules() {
	NodeRule definition=new_???();
	Rule params=Expressions();
	Rule body=Statements();
	Rule first=First();
	Rule suffix=Suffix();
	Rule suffix1=Suffix();
	Rule functionDecl=First();
	Rule templateParams=Expressions();
	Rule templateDecl=NonEmptyList();
	Rule maybeTemplate=Or();
	return Tag();
}
Rule CExpression_CRules() {
	LazyRule expression=new_???();
	expression.set();
	return expression;
}
Rule CFunctionSegment_CRules() {
	LazyRule rule=new_???();
	rule.set();
	return rule;
}
Rule CFunctionSegmentValue_CRules() {
	return Or();
}
Rule CFunctionStatement_CRules() {
	LazyRule functionStatement=new_???();
	functionStatement.set();
	return functionStatement;
}
Rule CFunctionStatementValue_CRules() {
	Rule expression=CExpression();
	return Or();
}
