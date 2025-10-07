// Generated transpiled C++ from 'src\main\java\magma\compile\JRules.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct JRules {};
Rule JDefinition_JRules() {
	Rule type=Node();
	Rule name=StrippedIdentifier();
	Rule modifiers=Delimited();
	Rule withModifiers=Split();
	Rule beforeName=Or();
	return Tag();
}
Rule JType_JRules() {
	LazyRule type=new_???();
	type.set();
	return type;
}
Rule JQualifiedName_JRules() {
	Rule segment=Tag();
	return Tag();
}
Rule JWildCard_JRules() {
	return Tag();
}
Rule JArray_JRules() {
	return Tag();
}
Rule Parameterized_JRules() {
	Rule arguments=Or();
	return Tag();
}
