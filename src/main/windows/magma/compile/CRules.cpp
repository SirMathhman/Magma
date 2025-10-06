// Generated transpiled C++ from 'src\main\java\magma\compile\CRules.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CRules {};
/*???*/ CRoot_CRules() {
	return Statements("", Strip("", Or(Lang.CStructure(), CFunction()), ""));
}
/*???*/ CFunction_CRules() {
	/*???*/ definition=new_???("", Lang.CDefinition());
	/*???*/ params=Expressions("", Or(Lang.CFunctionPointerDefinition(), Lang.CDefinition()));
	/*???*/ body=Statements("", CFunctionSegment());
	/*???*/ first=First(definition, "", params);
	/*???*/ suffix=Suffix(first, "");
	/*???*/ suffix1=Suffix(body, System.lineSeparator()+"");
	/*???*/ functionDecl=First(suffix, "", suffix1);
	/*???*/ templateParams=Expressions("", Prefix("", CommonRules.Identifier()));
	/*???*/ templateDecl=NonEmptyList("", Prefix("", Suffix(templateParams, ""+System.lineSeparator())));
	/*???*/ maybeTemplate=Or(templateDecl, Empty);
	return Tag("", First(maybeTemplate, "", functionDecl));
}
/*???*/ CExpression_CRules() {
	/*???*/ expression=new_???();
	expression.set(Or(Lang.Invocation(expression), Lang.FieldAccess(expression), Lang.Operator("", "", expression), Lang.Operator("", "", expression), Lang.Operator("", "", expression), Lang.StringExpr(), CommonRules.Identifier(), Lang.Char(), Lang.Invalid()));
	/*???*/ expression;
}
/*???*/ CFunctionSegment_CRules() {
	/*???*/ rule=new_???();
	rule.set(Or(Lang.Whitespace(), Prefix(System.lineSeparator()+"", CFunctionSegmentValue(rule)), Lang.Invalid()));
	/*???*/ rule;
}
/*???*/ CFunctionSegmentValue_CRules(/*???*/ rule) {
	return Or(Lang.LineComment(), Lang.Conditional("", CExpression(), rule), Lang.Conditional("", CExpression(), rule), Lang.Break(), Lang.Else(rule), CFunctionStatement(), Lang.Block(rule));
}
/*???*/ CFunctionStatement_CRules() {
	/*???*/ functionStatement=new_???();
	functionStatement.set(Or(Lang.Conditional("", CExpression(), functionStatement), Suffix(CFunctionStatementValue(), "")));
	/*???*/ functionStatement;
}
/*???*/ CFunctionStatementValue_CRules() {
	/*???*/ expression=CExpression();
	return Or(Lang.Return(expression), Lang.Invocation(expression), Lang.Initialization(Lang.CDefinition(), expression), Lang.CDefinition(), Lang.PostFix(expression));
}
