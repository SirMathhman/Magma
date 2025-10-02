// Generated transpiled C++ from 'src\main\java\magma\compile\rule\TypeFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct TypeFolder{};
template<>
/*@Override
	public DivideState*/ fold_TypeFolder(DivideState state, char c) {/*
		// Split on space when at depth 0
		if (c == ' ' && state.isLevel()) {
			return state.advance();
		}

		// Track depth for angle brackets (generics)
		if (c == '<') {
			return state.enter().append(c);
		}
		if (c == '>') {
			return state.exit().append(c);
		}

		// Track depth for parentheses (method params, etc.)
		if (c == '(') {
			return state.enter().append(c);
		}
		if (c == ')') {
			return state.exit().append(c);
		}

		// Append everything else
		return state.append(c);
	*/}
template<>
/*@Override
	public String*/ delimiter_TypeFolder() {/*
		return " ";
	*/}
