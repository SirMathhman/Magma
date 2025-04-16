/* import java.io.IOException; */
/* import java.nio.file.Files; */
/* import java.nio.file.Path; */
/* import java.nio.file.Paths; */
/* import java.util.Arrays; */
/* import java.util.Deque; */
/* import java.util.HashMap; */
/* import java.util.LinkedList; */
/* import java.util.Map; */
/* import java.util.function.BiFunction; */
/* import java.util.function.Consumer; */
/* import java.util.function.Function; */
/* import java.util.function.Supplier; */
/* import java.util.stream.Collectors; */
/* import java.util.stream.IntStream; */
/*  */
typedef struct {/* private final Deque<Character> queue; */
/* private final List_<String> segments; */
/* private String buffer; */
/* private int depth; */
/* private State(Deque<Character> queue, List_<String> segments, String buffer, int depth) {
            this.queue = queue;
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        } */
/* public State(Deque<Character> queue) {
            this(queue, Lists.empty(), "", 0);
        } */
/*  */

} State;
typedef struct {/*  */

} Joiner;
typedef struct {/* private static final Map<String, Function<List_<String>, Option<String>>> expandables = new HashMap<>(); */
/* private static final List_<Tuple<String, List_<String>>> visited = Lists.empty(); */
/* private static final List_<String> structs = Lists.empty(); */
/* private static final List_<String> methods = Lists.empty(); */
/* private static List_<Tuple<String, List_<String>>> toExpand = Lists.empty(); */
/*  */

} Main;
typedef struct {/* <R> Option<R> flatMap(Function<T, Option<R>> mapper); */
/* <R> Option<R> map(Function<T, R> mapper); */
/*  */

} Option_char_ref;
typedef struct {/*  */

} List__char_ref;
typedef struct {/*  */

} Tuple_char_ref_List__char_ref;
typedef struct {/* <R> Option<R> flatMap(Function<T, Option<R>> mapper); */
/* <R> Option<R> map(Function<T, R> mapper); */
/*  */

} Option_List__char_ref;
typedef struct {/* <R> Option<R> flatMap(Function<T, Option<R>> mapper); */
/* <R> Option<R> map(Function<T, R> mapper); */
/*  */

} Option_State;
typedef struct {/* <R> R fold(R initial, BiFunction<R, T, R> folder) {
            R current = initial;
            while (true) {
                R finalCurrent = current;
                Option<R> option = this.head.next().map(next -> folder.apply(finalCurrent, next));
                if (option.isPresent()) {
                    current = option.orElse(null);
                }
                else {
                    return current;
                }
            }
        } */
/* <R> Iterator<R> map(Function<T, R> mapper) {
            return new Iterator<>(() -> this.head.next().map(mapper));
        } */
/* <C> C collect(Collector<T, C> collector) {
            return this.fold(collector.createInitial(), collector::fold);
        } */
/*  */

} Iterator_char_ref;
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
Option_char_ref Joiner_createInitial(){
}
Option_char_ref Joiner_fold(Option_char_ref current, char* element){
}
void __main__(char** args){
}
char* Main_compile(char* input){
}
List__char_ref Main_assemble(List__char_ref compiled){
}
void Main_assembleGenerics(List__char_ref compiled){
}
Option_char_ref Main_assembleEntry(Tuple_char_ref_List__char_ref expansion){
}
Option_char_ref Main_compileStatements(char* input, Option_char_ref (*compiler)(char*)){
}
Option_char_ref Main_compileAndMergeAll(List__char_ref segments, Option_char_ref (*compiler)(char*), char* (*merger)(char*, char*)){
}
char* Main_mergeAll(List__char_ref list, char* (*merger)(char*, char*)){
}
Option_List__char_ref Main_compileAll(List__char_ref segments, Option_char_ref (*compiler)(char*)){
}
char* Main_mergeStatements(char* output, char* element){
}
List__char_ref Main_divideAll(char* input, State (*folder)(State, char)){
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
Option_char_ref Main_assembleStruct(char* name, char* inputContent, List__char_ref typeParams, List__char_ref typeArguments){
}
int Main_isSymbol(char* input){
}
char* Main_compileClassSegment(char* input, char* structName, List__char_ref typeParams, List__char_ref typeArguments){
}
Option_char_ref Main_compileMethod(char* input, char* structName, List__char_ref typeParams, List__char_ref typeArguments){
}
Option_char_ref Main_compileValues(char* input, Option_char_ref (*compileDefinition)(char*)){
}
List__char_ref Main_divideValues(char* input){
}
State Main_foldValueChar(State state, char c){
}
char* Main_mergeValues(char* builder, char* element){
}
char* Main_mergeDelimited(char* buffer, char* element, char* delimiter){
}
Option_char_ref Main_compileDefinition(char* definition, List__char_ref stack, List__char_ref typeParams, List__char_ref typeArguments){
}
int Main_findTypeSeparator(char* input){
}
Option_char_ref Main_compileType(char* input, Option_char_ref maybeName, List__char_ref typeParams, List__char_ref typeArguments){
}
char* Main_stringify(char* base, List__char_ref arguments){
}
char* Main_generateFunctionalDefinition(Option_char_ref name, List__char_ref paramTypes, char* returnType){
}
char* Main_generateSimpleDefinition(char* type, Option_char_ref maybeName){
}
char* Main_generatePlaceholder(char* input){
}
char* Option_char_ref_orElse(char* other){
}
Option_char_ref Option_char_ref_or(Option_char_ref (*other)()){
}
char* Option_char_ref_orElseGet(char* (*other)()){
}
void Option_char_ref_ifPresent(void (*consumer)(char*)){
}
int Option_char_ref_isPresent(){
}
List__char_ref List__char_ref_add(char* element){
}
List__char_ref List__char_ref_addAll(List__char_ref elements){
}
int List__char_ref_isEmpty(){
}
List__char_ref List__char_ref_copy(){
}
int List__char_ref_contains(char* element){
}
Iterator_char_ref List__char_ref_iter(){
}
int List__char_ref_indexOf(char* element){
}
char* List__char_ref_get(int index){
}
List__char_ref Option_List__char_ref_orElse(List__char_ref other){
}
Option_List__char_ref Option_List__char_ref_or(Option_List__char_ref (*other)()){
}
List__char_ref Option_List__char_ref_orElseGet(List__char_ref (*other)()){
}
void Option_List__char_ref_ifPresent(void (*consumer)(List__char_ref)){
}
int Option_List__char_ref_isPresent(){
}
State Option_State_orElse(State other){
}
Option_State Option_State_or(Option_State (*other)()){
}
State Option_State_orElseGet(State (*other)()){
}
void Option_State_ifPresent(void (*consumer)(State)){
}
int Option_State_isPresent(){
}
int main(int argc, char **argv){
	__main__(argv);
	return 0;
}