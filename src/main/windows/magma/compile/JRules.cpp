// Generated transpiled C++ from 'src\main\java\magma\compile\JRules.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct JRules {};
Rule JDefinition_JRules() {
	Rule type=Node("", JType());
	Rule name=StrippedIdentifier("");
	Rule modifiers=Delimited("", Tag("", String("")), "");
	Rule withModifiers=Split(modifiers, KeepLast(new_???(new_???())), type);
	Rule beforeName=Or(withModifiers, type);
	return Tag("", Strip(Last(beforeName, "", name)));
}
Rule JType_JRules() {
	LazyRule type=new_???();
	type.set(Or(Parameterized("", type, Node("", JQualifiedName())), JArray(type), CommonRules.Identifier(), JWildCard(), Tag("", Strip(Suffix(Node("", type), ""))), JQualifiedName()));
	return type;
}
Rule JQualifiedName_JRules() {
	Rule segment=Tag("", StrippedIdentifier(""));
	return Tag("", Strip(NodeListRule.Delimited("", segment, "")));
}
Rule JWildCard_JRules() {
	return Tag("", Strip(Prefix("", Empty)));
}
Rule JArray_JRules(Rule type) {
	return Tag("", Strip(Suffix(Node("", type), "")));
}
Rule Parameterized_JRules(String tag, Rule type, Rule base) {
	Rule arguments=Or(Expressions("", type), Strip(Empty));
	return Tag(tag, Strip(Suffix(First(base, "", arguments), "")));
}
