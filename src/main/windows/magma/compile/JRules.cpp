// Generated transpiled C++ from 'src\main\java\magma\compile\JRules.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct JRules {};
Rule JDefinition_JRules() {
	Rule type=Node("", JType());
	Rule name=CommonRules.StrippedIdentifier("");
	Rule modifiers=Delimited("", Tag("", String("")), "");
	Rule withModifiers=Split(modifiers, KeepLast(new_???(new_???())), type);
	Rule beforeName=Or(withModifiers, type);
	return Tag("", Strip(Last(beforeName, "", name)));
}
Rule JType_JRules() {
	LazyRule type=new_???();
	type.set(Or(JGeneric(type), JArray(type), CommonRules.Identifier(), JWildCard(), Tag("", Strip(Suffix(Node("", type), ""))), Tag("", Strip(Last(Node("", type), "", CommonRules.StrippedIdentifier(""))))));
	return type;
}
Rule JWildCard_JRules() {
	return Tag("", Strip(Prefix("", Empty)));
}
Rule JArray_JRules(Rule type) {
	return Tag("", Strip(Suffix(Node("", type), "")));
}
Rule JGeneric_JRules(Rule type) {
	Rule base=CommonRules.StrippedIdentifier("");
	Rule arguments=Or(Expressions("", type), Strip(Empty));
	return Tag("", Strip(Suffix(First(base, "", arguments), "")));
}
