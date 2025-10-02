// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SuffixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SuffixRule{Rule rule;, char* suffix;};
Rule Suffix_SuffixRule(Rule rule, char* suffix) {/*
		return new SuffixRule(rule, suffix);*/}
Result<Node, CompileError> lex_SuffixRule(char* input) {/*
		if (!input.endsWith(suffix()))
			return new Err<>(new CompileError("Suffix '" + suffix + "' not present", new StringContext(input)));*//*
		final String slice = input.substring(0, input.length() - suffix().length());*//*
		return getRule().lex(slice);*/}
Result<String, CompileError> generate_SuffixRule(Node node) {/*
		return rule.generate(node).mapValue(value -> value + suffix());*/}
Rule getRule_SuffixRule() {/*
		return rule;*/}
