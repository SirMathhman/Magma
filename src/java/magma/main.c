struct DivideState {/* private final List<String> segments; *//* private int depth; *//* private StringBuilder buffer; *//* private DivideState(List<String> segments, StringBuilder buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public DivideState() {
            this(new ArrayList<>(), new StringBuilder(), 0);
        }

        private Stream<String> stream() {
            return this.segments.stream();
        }

        private DivideState advance() {
            this.segments.add(this.buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }

        private DivideState append(char c) {
            this.buffer.append(c);
            return this;
        }

        public boolean isLevel() {
            return this.depth == 0;
        }

        public DivideState enter() {
            this.depth++;
            return this;
        }

        public DivideState exit() {
            this.depth--;
            return this;
        }
    }

    private record Tuple<A, B>(A left, B right) {
    }

    private record CompilerState(List<String> structs) {
        public CompilerState() {
            this(new ArrayList<>());
        }

        public CompilerState add(String element) {
            ArrayList<String> copy = new ArrayList<>(this.structs);
            copy.add(element);
            return new CompilerState(copy);
        }
    }

    public static void main(String[] args) {
        try {
            Path source = Paths.get(".", "src", "java", "magma", "Main.java");
            String input = Files.readString(source);
            Path target = source.resolveSibling("main.c");
            Files.writeString(target, compile(input));
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        Tuple<CompilerState, String> tuple = compileStatements(input, new CompilerState(), Main::compileRootSegment); *//* CompilerState elements = tuple.left.add(tuple.right); *//* String joined = String.join("", elements.structs); *//* return joined + "int main(){\n\treturn 0;\n}\n"; *//* }

    private static Tuple<CompilerState, String> compileStatements(String input, CompilerState structs, BiFunction<CompilerState, String, Tuple<CompilerState, String>> compiler) {
        return divideStatements(input).reduce(new Tuple<>(structs, ""), (tuple, element) -> foldSegment(tuple, element, compiler), (_, next) -> next); *//* }

    private static Tuple<CompilerState, String> foldSegment(Tuple<CompilerState, String> tuple, String element, BiFunction<CompilerState, String, Tuple<CompilerState, String>> compiler) {
        CompilerState currentStructs = tuple.left; *//* String currentOutput = tuple.right; *//* Tuple<CompilerState, String> compiledStruct = compiler.apply(currentStructs, element); *//* CompilerState compiledStructs = compiledStruct.left; *//* String compiledElement = compiledStruct.right; *//* return new Tuple<>(compiledStructs, currentOutput + compiledElement); *//* }

    private static Stream<String> divideStatements(String input) {
        DivideState current = new DivideState(); *//* for (int i = 0; *//* i < input.length(); *//* i++) {
            char c = input.charAt(i);
            current = divideStatementChar(current, c);
        }
        return current.advance().stream(); *//* }

    private static DivideState divideStatementChar(DivideState divideState, char c) {
        DivideState appended = divideState.append(c); *//* if (c == '; *//* ' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '{') {
            return appended.enter();
        }
        if (c == '}') {
            return appended.exit();
        }
        return appended; *//* }

    private static Tuple<CompilerState, String> compileRootSegment(CompilerState structs, String input) {
        String stripped = input.strip(); *//* if (stripped.startsWith("package ")) {
            return new Tuple<>(structs, "");
        }

        if (stripped.startsWith("import ")) {
            return new Tuple<>(structs, "// #include <temp.h>\n");
        }

        int classIndex = stripped.indexOf("class "); *//* if (classIndex >= 0) {
            String afterKeyword = stripped.substring(classIndex + "class ".length());
            int contentStart = afterKeyword.indexOf("{");
            if (contentStart >= 0) {
                String name = afterKeyword.substring(0, contentStart).strip();
                String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
                if (withEnd.endsWith("}")) {
                    String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
                    Tuple<CompilerState, String> outputTuple = compileStatements(inputContent, structs, Main::compileRootSegment);
                    CompilerState outputStructs = outputTuple.left;
                    String outputContent = outputTuple.right;

                    String generated = "struct %s {%s\n};\n".formatted(name, outputContent);
                    CompilerState withGenerated = outputStructs.add(generated);
                    return new Tuple<>(withGenerated, "");
                }
            }
        }

        return new Tuple<>(structs, "/* " + stripped + " */"); *//*  */
};
struct Main {
};
// #include <temp.h>
// #include <temp.h>
// #include <temp.h>
// #include <temp.h>
// #include <temp.h>
// #include <temp.h>
// #include <temp.h>
// #include <temp.h>
int main(){
	return 0;
}
