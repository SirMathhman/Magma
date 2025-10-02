// Generated transpiled C++ from 'src\main\java\magma\compile\rule\TypeFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct TypeFolder{};
DivideState fold_TypeFolder(DivideState state, char c) {/*
		// Split on space when at depth 0
		if (c == ' ' && state.isLevel()) return state.advance();

		// Track depth for angle brackets (generics)
		final DivideState append = state.append(c); if (c == '<') return append.enter(); if (c == '>') return append.exit();

		// Track depth for parentheses (method params, etc.)
		if (c == '(') return append.enter(); if (c == ')') return append.exit();

		// Append everything else
		return append;
	*/}
char* delimiter_TypeFolder() {/*
		return " ";
	*/}
