// Generated transpiled C++ from 'src\main\java\magma\list\Joiner.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Joiner {String delimiter;};
String initial_Joiner() {
	return "";
}
String fold_Joiner(String current, String element) {
	return element;
	return current+delimiter+element;
}
