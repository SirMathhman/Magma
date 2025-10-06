// Generated transpiled C++ from 'src\main\java\magma\compile\CRules.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CRules {};
Rule CRoot_CRules() {
	return Statements("", Strip("", Or(Lang.CStructure(), CFunction()), ""));
}
Rule CFunction_CRules() {
	NodeRule definition=new_???("", Lang.CDefinition());
	Rule params=Expressions("", Or(Lang.CFunctionPointerDefinition(), Lang.CDefinition()));
	Rule body=Statements("", CFunctionSegment());
	Rule first=First(definition, "", params);
	Rule suffix=Suffix(first, "");
	Rule suffix1=Suffix(body, System.lineSeparator()+"");
	Rule functionDecl=First(suffix, "", suffix1);
	Rule templateParams=Expressions("", Prefix("", CommonRules.Identifier()));
	Rule templateDecl=NonEmptyList("", Prefix("", Suffix(templateParams, ""+System.lineSeparator())));
	Rule maybeTemplate=Or(templateDecl, Empty);
	return Tag("", First(maybeTemplate, "", functionDecl));
}
Rule CExpression_CRules() {
	LazyRule expression=new_???();
	expression.set(Or(Lang.Invocation(expression), Lang.FieldAccess(expression), Lang.Operator("", "", expression), Lang.Operator("", "", expression), Lang.Operator("", "", expression), Lang.StringExpr(), CommonRules.Identifier(), Lang.Char(), Lang.Invalid()));
	return expression;
}
Rule CFunctionSegment_CRules() {
	LazyRule rule=new_???();
	rule.set(Or(Lang.Whitespace(), Prefix(System.lineSeparator()+"", CFunctionSegmentValue(rule)), Lang.Invalid()));
	return rule;
}
Rule CFunctionSegmentValue_CRules(LazyRule rule) {
	return Or(Lang.LineComment(), Lang.Conditional("", CExpression(), rule), Lang.Conditional("", CExpression(), rule), Lang.Break(), Lang.Else(rule), CFunctionStatement(), Lang.Block(rule));
}
Rule CFunctionStatement_CRules() {
	LazyRule functionStatement=new_???();
	functionStatement.set(Or(Lang.Conditional("", CExpression(), functionStatement), Suffix(CFunctionStatementValue(), "")));
	return functionStatement;
}
Rule CFunctionStatementValue_CRules() {
	Rule expression=CExpression();
	return Or(Lang.Return(expression), Lang.Invocation(expression), Lang.Initialization(Lang.CDefinition(), expression), Lang.CDefinition(), Lang.PostFix(expression));
}
