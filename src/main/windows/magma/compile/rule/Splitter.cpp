// Generated transpiled C++ from 'src\main\java\magma\compile\rule\Splitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct Splitter{/**
	 * Split the input string into left and right parts.
	 * Returns None if splitting is not possible.
	 * Returns Some(Tuple(left, right)) if splitting succeeds.
	 */
	Option<Tuple<String, String>> split(String input);};
