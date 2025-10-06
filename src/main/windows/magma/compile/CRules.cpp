// Generated transpiled C++ from 'src\main\java\magma\compile\CRules.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CRules {};
Rule CRoot_CRules() {
	return Statements("", Strip("", Or(Lang.CStructure(), CFunction()), ""));
}
Rule CFunction_CRules() {
	NodeRule definition=new_???("", Lang.CDefinition());
	Rule params=Expressions("", Or(Lang.CFunctionPointerDefinition(), Lang.CDefinition()));
	Rule body=Statements("", Lang.CFunctionSegment());
	Rule first=First(definition, "", params);
	Rule suffix=Suffix(first, "");
	Rule suffix1=Suffix(body, System.lineSeparator()+"");
	Rule functionDecl=First(suffix, "", suffix1);
	Rule templateParams=Expressions("", Prefix("", CommonRules.Identifier()));
	Rule templateDecl=NonEmptyList("", Prefix("", Suffix(templateParams, ""+System.lineSeparator())));
	Rule maybeTemplate=Or(templateDecl, Empty);
	return Tag("", First(maybeTemplate, "", functionDecl));
}
