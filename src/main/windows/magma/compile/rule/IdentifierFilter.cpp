// Generated transpiled C++ from 'src\main\java\magma\compile\rule\IdentifierFilter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct IdentifierFilter {};
/*???*/ IdentifierFilter_IdentifierFilter() {
}
/*???*/ test_IdentifierFilter(/*???*/ input) {
	return IntStream.range(/*???*/, input.length()).mapToObj(/*???*/).allMatch(/*???*/=='_');
}
/*???*/ createErrorMessage_IdentifierFilter() {
	return "";
}
