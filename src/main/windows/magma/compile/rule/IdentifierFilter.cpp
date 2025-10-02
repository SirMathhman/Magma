// Generated transpiled C++ from 'src\main\java\magma\compile\rule\IdentifierFilter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct IdentifierFilter {};
/*public static Filter Identifier = new*/ IdentifierFilter_IdentifierFilter() {
}
/*@Override
	public boolean*/ test_IdentifierFilter(/*String*/ input) {
	/*return IntStream.range*/(/*0*/, /* input.length()).mapToObj(input::charAt).allMatch(Character::isLetterOrDigit)*/;
}
/*@Override
	public String*/ createErrorMessage_IdentifierFilter() {
	return /*"Not an identifier"*/;
}
