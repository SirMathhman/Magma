// Generated transpiled C++ from 'src\main\java\magma\compile\rule\IdentifierFilter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct IdentifierFilter {};
new IdentifierFilter_IdentifierFilter() {
}
boolean test_IdentifierFilter(char* input) {
	return IntStream.range(/*???*/, input.length()).mapToObj(/*???*/).allMatch(/*???*/=='_');
}
char* createErrorMessage_IdentifierFilter() {
	return "";
}
