#include <temp.h>
#include <temp.h>
#include <temp.h>
#include <temp.h>
#include <temp.h>
#include <temp.h>
#include <temp.h>
#include <temp.h>
#include <temp.h>
#include <temp.h>
#include <temp.h>
struct Main {
};
void main(String[] args){
}
Result<String, CompileException> compile(String input){
}
Result<String, CompileException> compileAll(String input, Function<String, Result<String, CompileException>> compiler){
}
Result<String, CompileException> compileAll(List<String> segments, Function<String, Result<String, CompileException>> compiler, BiFunction<StringBuilder, String, StringBuilder> getAppend){
}
StringBuilder mergeStatements(StringBuilder cache, String element){
}
List<String> divideByStatements(String input){
}
List<String> divideUsing(String input, BiFunction<State, Character, State> divider){
}
State divideCharUsing(State state, char next, BiFunction<State, Character, State> divider){
}
State divideStatementChar(State state, char next){
}
Optional<State> divideSingleQuotesChar(State state, char next){
}
Result<String, CompileException> compileRootSegment(String segment){
}
Result<String, CompileException> invalidateInput(String type, String input){
}
Result<String, CompileException> compileClassSegment(String input){
}
Result<String, CompileException> compileMethod(String input){
}
List<String> divideByValues(String paramString){
}
State divideValueChar(State state, Character c){
}
StringBuilder mergeValues(StringBuilder cache, String element){
}
Result<String, CompileException> compileDefinition(String input){
}
Result<Node, CompileException> parseSplit(String input, String infix){
}
Optional<Integer> locateTypeSeparator(String input){
}
Result<String, CompileException> generateDefinition(Node node){
}
Result<T, CompileException> createMissingInfixError(String input, String infix){
}
