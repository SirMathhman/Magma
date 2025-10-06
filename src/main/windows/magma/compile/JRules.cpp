// Generated transpiled C++ from 'src\main\java\magma\compile\JRules.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct JRules {};
/*???*/ JDefinition_JRules() {
	/*???*/ type=Node("", JType());
	/*???*/ name=StrippedIdentifier("");
	/*???*/ modifiers=Delimited("", Tag("", String("")), "");
	/*???*/ withModifiers=Split(modifiers, KeepLast(new_???(new_???())), type);
	/*???*/ beforeName=Or(withModifiers, type);
	return Tag("", Strip(Last(beforeName, "", name)));
}
/*???*/ JType_JRules() {
	/*???*/ type=new_???();
	type.set(Or(Parameterized("", type, Node("", JQualifiedName())), JArray(type), CommonRules.Identifier(), JWildCard(), Tag("", Strip(Suffix(Node("", type), ""))), JQualifiedName()));
	/*???*/ type;
}
/*???*/ JQualifiedName_JRules() {
	/*???*/ segment=Tag("", StrippedIdentifier(""));
	return Tag("", Strip(NodeListRule.Delimited("", segment, "")));
}
/*???*/ JWildCard_JRules() {
	return Tag("", Strip(Prefix("", Empty)));
}
/*???*/ JArray_JRules(/*???*/ type) {
	return Tag("", Strip(Suffix(Node("", type), "")));
}
/*???*/ Parameterized_JRules(/*???*/ tag, /*???*/ type, /*???*/ base) {
	/*???*/ arguments=Or(Expressions("", type), Strip(Empty));
	return Tag(tag, Strip(Suffix(First(base, "", arguments), "")));
}
