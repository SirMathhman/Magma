// Generated transpiled C++ from 'src\main\java\magma\compile\rule\IdentifierFilter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct IdentifierFilter {};
new IdentifierFilter_IdentifierFilter() {
}
boolean test_IdentifierFilter(char* input) {
	return IntStream.range(0, input.length()).mapToObj(input::charAt).allMatch(ch -> Character.isLetterOrDigit(ch) | ch == '_');
}
char* createErrorMessage_IdentifierFilter() {
	an identifier";
}
