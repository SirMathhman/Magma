/* import java.io.IOException; */
/* import java.nio.file.Files; */
/* import java.nio.file.Path; */
/* import java.nio.file.Paths; */
/* import java.util.ArrayList; */
/* import java.util.Arrays; */
/* import java.util.Collections; */
/* import java.util.Deque; */
/* import java.util.HashMap; */
/* import java.util.LinkedList; */
/* import java.util.List; */
/* import java.util.Map; */
/* import java.util.function.BiFunction; */
/* import java.util.function.Consumer; */
/* import java.util.function.Function; */
/* import java.util.function.Supplier; */
/* import java.util.stream.Collectors; */
/* import java.util.stream.IntStream; */
/*  */
typedef struct {/* private final Deque<Character> queue; */
/* private final List<String> segments; */
/* private String buffer; */
/* private int depth; */
/* private State(Deque<Character> queue, List<String> segments, String buffer, int depth) {
            this.queue = queue;
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        } */
/* public State(Deque<Character> queue) {
            this(queue, new ArrayList<>(), "", 0);
        } */
/*  */

} State;
typedef struct {/* private static final Map<String, Function<List<String>, Option<String>>> expandables = new HashMap<>(); */
/* private static final List<Tuple<String, List<String>>> visited = new ArrayList<>(); */
/* private static final List<String> structs = new ArrayList<>(); */
/* private static final List<String> methods = new ArrayList<>(); */
/* private static List<Tuple<String, List<String>>> toExpand = new ArrayList<>(); */
/*  */

} Main;
typedef struct {/*  */

} Option_char_ref;
typedef struct {/*  */

} Tuple_char_ref_List_char_ref;
typedef struct {/*  */

} Option_List_char_ref;
typedef struct {/*  */

} Option_State;
typedef struct {/*  */

} Option_R;
int State_isShallow(){
}
State State_exit(){
}
State State_enter(){
}
State State_advance(){
}
int State_isLevel(){
}
State State_append(char c){
}
int State_hasNext(){
}
char State_pop(){
}
void __main__(char** args){
}
char* Main_compile(char* input){
}
List_char_ref Main_assemble(List_char_ref compiled){
}
void Main_assembleGenerics(List_char_ref compiled){
}
Option_char_ref Main_assembleEntry(Tuple_char_ref_List_char_ref expansion){
}
Option_char_ref Main_compileStatements(char* input, Option_char_ref (*compiler)(char*)){
}
Option_char_ref Main_compileAndMergeAll(List_char_ref segments, Option_char_ref (*compiler)(char*), char* (*merger)(char*, char*)){
}
char* Main_mergeAll(List_char_ref list, char* (*merger)(char*, char*)){
}
Option_List_char_ref Main_compileAll(List_char_ref segments, Option_char_ref (*compiler)(char*)){
}
char* Main_mergeStatements(char* output, char* element){
}
List_char_ref Main_divideAll(char* input, State (*folder)(State, char)){
}
Option_State Main_foldDoubleQuotes(State current, char c){
}
Option_State Main_foldSingleQuotes(State current, char c){
}
State Main_foldStatementChar(State state, char c){
}
char* Main_compileRootSegment(char* input){
}
Option_char_ref Main_compileClass(char* input){
}
Option_char_ref Main_compileToStruct(char* input, char* infix){
}
Option_char_ref Main_assembleStruct(char* name, char* inputContent, List_char_ref typeParams, List_char_ref typeArguments){
}
int Main_isSymbol(char* input){
}
char* Main_compileClassSegment(char* input, char* structName, List_char_ref typeParams, List_char_ref typeArguments){
}
Option_char_ref Main_compileMethod(char* input, char* structName, List_char_ref typeParams, List_char_ref typeArguments){
}
Option_char_ref Main_compileValues(char* input, Option_char_ref (*compileDefinition)(char*)){
}
List_char_ref Main_divideValues(char* input){
}
State Main_foldValueChar(State state, char c){
}
char* Main_mergeValues(char* builder, char* element){
}
char* Main_mergeDelimited(char* buffer, char* element, char* delimiter){
}
Option_char_ref Main_compileDefinition(char* definition, List_char_ref stack, List_char_ref typeParams, List_char_ref typeArguments){
}
int Main_findTypeSeparator(char* input){
}
Option_char_ref Main_compileType(char* input, Option_char_ref maybeName, List_char_ref typeParams, List_char_ref typeArguments){
}
char* Main_stringify(char* base, List_char_ref arguments){
}
char* Main_generateFunctionalDefinition(Option_char_ref name, List_char_ref paramTypes, char* returnType){
}
char* Main_generateSimpleDefinition(char* type, Option_char_ref maybeName){
}
char* Main_generatePlaceholder(char* input){
}
Option_char_ref Option_char_ref_of(char* value){
}
Option_char_ref Option_char_ref_empty(){
}
char* Option_char_ref_orElse(char* other){
}
Option_R Option_char_ref_flatMap(Option_R (*mapper)(char*)){
}
Option_R Option_char_ref_map(R (*mapper)(char*)){
}
Option_char_ref Option_char_ref_or(Option_char_ref (*other)()){
}
char* Option_char_ref_orElseGet(char* (*other)()){
}
void Option_char_ref_ifPresent(Consumer_char_ref consumer){
}
Option_List_char_ref Option_List_char_ref_of(List_char_ref value){
}
Option_List_char_ref Option_List_char_ref_empty(){
}
List_char_ref Option_List_char_ref_orElse(List_char_ref other){
}
Option_R Option_List_char_ref_flatMap(Option_R (*mapper)(List_char_ref)){
}
Option_R Option_List_char_ref_map(R (*mapper)(List_char_ref)){
}
Option_List_char_ref Option_List_char_ref_or(Option_List_char_ref (*other)()){
}
List_char_ref Option_List_char_ref_orElseGet(List_char_ref (*other)()){
}
void Option_List_char_ref_ifPresent(Consumer_List_char_ref consumer){
}
Option_State Option_State_of(State value){
}
Option_State Option_State_empty(){
}
State Option_State_orElse(State other){
}
Option_R Option_State_flatMap(Option_R (*mapper)(State)){
}
Option_R Option_State_map(R (*mapper)(State)){
}
Option_State Option_State_or(Option_State (*other)()){
}
State Option_State_orElseGet(State (*other)()){
}
void Option_State_ifPresent(Consumer_State consumer){
}
Option_R Option_R_of(R value){
}
Option_R Option_R_empty(){
}
R Option_R_orElse(R other){
}
Option_R Option_R_flatMap(Option_R (*mapper)(R)){
}
Option_R Option_R_map(R (*mapper)(R)){
}
Option_R Option_R_or(Option_R (*other)()){
}
R Option_R_orElseGet(R (*other)()){
}
void Option_R_ifPresent(Consumer_R consumer){
}
int main(int argc, char **argv){
	__main__(argv);
	return 0;
}