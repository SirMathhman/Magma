// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DelimitedRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DelimitedRule {/*???*/ delimiter;};
Stream</*???*/> divide_DelimitedRule(/*???*/ input) {
	return new_???(new_???(input.split(Pattern.quote(delimiter))));
}
