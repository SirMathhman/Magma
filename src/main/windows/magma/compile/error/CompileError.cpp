// Generated transpiled C++ from 'src\main\java\magma\compile\error\CompileError.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CompileError {/*String*/ reason;/* Context*/ context;/* List<CompileError>*/ causes;};
/*public*/ CompileError_CompileError(/*String*/ reason, /* Context*/ sourceCode) {
	/*this*/(/*reason*/, /* sourceCode*/, /* Collections.emptyList())*/;
}
/*@Override
	public String*/ display_CompileError() {
	/*return format*/(/*0*/, /* 0)*/;
}
/*private String*/ format_CompileError(/*int*/ depth, /* int*/ index) {
	/*final ArrayList<CompileError> copy */=/* new ArrayList<>(causes)*/;
	/*copy.sort*/(/*Comparator.comparingInt(CompileError::depth))*/;
	/*final String formattedChildren */=/* joinErrors(depth, copy)*/;
	/*final String s */=/* depth == 0 ? "" : System.lineSeparator() + "\t".repeat(depth)*/;
	/*return s + index + ") " + reason + ": " + context.display*/(/*depth) + formattedChildren*/;
}
/*private String*/ joinErrors_CompileError(/*int*/ depth, /* List<CompileError>*/ copy) {
	/*return IntStream.range*/(/*0*/, /* copy.size())
										.mapToObj(index -> formatChild(depth, copy, index))
										.collect(Collectors.joining())*/;
}
/*private String*/ formatChild_CompileError(/*int*/ depth, /* List<CompileError>*/ copy, /* int*/ i) {
	/*CompileError error */=/* copy.get(i)*/;
	/*return error.format*/(/*depth + 1*/, /* i)*/;
}
/*private int*/ depth_CompileError() {
	/*return 1 + causes.stream*/(/*).mapToInt(CompileError::depth).max().orElse(0)*/;
}
