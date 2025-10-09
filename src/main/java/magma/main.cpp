struct Main {
};
enum OptionTag {
	Some,
	None
};
template<typename T>
union OptionData {
	Some<T> some;
	None<T> none;
};
template<typename T>
struct Option {
	OptionTag tag;
	OptionData<T> data;
};
T (*orElse)(T);
T (*orElseGet)(/*Supplier<T>*/);
/*Option<T>*/ (*or)(/*Supplier<Option<T>>*/);
/*Option<R>*/ (*map)(/*R>*/);
/**/

template<typename T>
struct Head {
};
/*Option<T>*/ (*next)();
/**/

template<typename T>
struct List {
};
/*List<T>*/ (*clear)();
/*List<T>*/ (*add)(T);
int (*size)();
/*Option<T>*/ (*get)(int);
/*Stream<T>*/ (*stream)();
/**/

template<typename T, C>
struct Collector {
};
C (*createInitial)();
C (*fold)(C, T);
/**/

struct JDefined {
};
String (*generate)();
/**/

/*public record Some<T>(T value) implements Option<T>*/ {
};
/*@Override
		public T orElse(T other)*/ {
};
return this.value;
/**/

/*@Override
		public T orElseGet(Supplier<T> other)*/ {
};
return this.value;
/**/

/*@Override
		public Option<T> or(Supplier<Option<T>> other)*/ {
};
return this;
/**/

/*@Override
		public <R> Option<R> map(Function<T, R> mapper)*/ {
};
/*return*/ new Some<R>(mapper.apply(this.value));
/**/

/**/

struct None<T> implements Option<T> {
};
/*@Override
		public T orElse(T other)*/ {
};
return other;
/**/

/*@Override
		public T orElseGet(Supplier<T> other)*/ {
};
return (*other.get)();
/**/

/*@Override
		public Option<T> or(Supplier<Option<T>> other)*/ {
};
return (*other.get)();
/**/

/*@Override
		public <R> Option<R> map(Function<T, R> mapper)*/ {
};
new (*None<R>)();
/**/

/**/

struct FlatMapHead<T, R> implements Head<R> {
};
/*private final*/ /*Head<T>*/ head;
/*private final Function<T,*/ /*Stream<R>>*/ mapper;
new (*None<Stream<R>>)();
/*public FlatMapHead(Head<T> head, Function<T, Stream<R>> mapper)*/ {
};
/*this.head*/ /*=*/ head;
/*this.mapper*/ /*=*/ mapper;
/**/

/*@Override
		public Option<R> next()*/ {
};
/*while (true)*/ {
};
/*if (this.current instanceof None<Stream<R>>)*/ {
};
/*=*/ (*this.head.next)();
/*if (sOpt instanceof None<T>) return*/ new None<R>();
/*else if (sOpt instanceof Some<T>(T s)) this.current =*/ new Some<Stream<R>>(this.mapper.apply(s));
/**/

/*if (this.current instanceof Some<Stream<R>>(Stream<R> stream))*/ {
};
/*Option<R> tOpt*/ /*=*/ stream.head().next();
/*if (tOpt instanceof Some<R>)*/ return tOpt;
new (*None<Stream<R>>)();
/**/

/**/

/**/

/**/

struct Stream<T> {
	/*Head<T>*/ head;
};
/*public <R> Stream<R> map(Function<T, R> mapper)*/ {
};
/*return new Stream<R>(()*/ /*->*/ this.head.next().map(mapper));
/**/

/*public <C> C collect(Collector<T, C> collector)*/ {
};
/*return*/ /*this.fold(collector.createInitial(),*/ collector::fold);
/**/

/*public <C> C fold(C initial, BiFunction<C, T, C> folder)*/ {
};
/*while (true) if (this.head.next() instanceof Some<T>(T value)) initial =*/ /*folder.apply(initial,*/ value);
/*else*/ return initial;
/**/

/*public <R> Stream<R> flatMap(Function<T, Stream<R>> mapper)*/ {
};
/*return new Stream<R>(new FlatMapHead<T,*/ /*R>(this.head,*/ mapper));
/**/

/**/

struct ArrayHead<T> implements Head<T> {
};
/*private final*/ /*T[]*/ array;
/*private final*/ int size;
/*private int counter*/ /*=*/ 0;
/*private ArrayHead(T[] array, int size)*/ {
};
/*this.array*/ /*=*/ array;
/*this.size*/ /*=*/ size;
/**/

/*@Override
		public Option<T> next()*/ {
};
/*if (this.counter >= this.size) return*/ new None<T>();
/*final T next*/ /*=*/ this.array[this.counter];
/*this.counter++*/;
new (*Some<T>)();
/**/

/**/

struct ArrayList<T> implements List<T> {
};
/*private final*/ /*Supplier<T>*/ createDefault;
/*private*/ /*T[]*/ array;
/*private*/ int size;
/*public ArrayList(T[] array, Supplier<T> createDefault)*/ {
};
/*this.array*/ /*=*/ array;
/*this.createDefault*/ /*=*/ createDefault;
/*this.size*/ /*=*/ 0;
/**/

/*public ArrayList(Supplier<T> createDefault)*/ {
};
/*this(alloc(10),*/ createDefault);
/**/

/*@Override
		public List<T> clear()*/ {
};
/*this.size*/ /*=*/ 0;
return this;
/**/

/*@Override
		public List<T> add(T element)*/ {
};
return (*this.set)();
/**/

/*@Override
		public int size()*/ {
};
return this.size;
/**/

/*@Override
		public Option<T> get(int index)*/ {
};
/*if (index < this.size) return*/ new Some<T>(this.array[index]);
new (*None<T>)();
/**/

/*@Override
		public Stream<T> stream()*/ {
};
/*return new Stream<T>(new*/ /*ArrayHead<T>(this.array,*/ this.size));
/**/

/*private List<T> set(int index, T element)*/ {
};
/*=*/ (*this.resizeArrayToContainIndex)();
/*if (option instanceof Some<T[]>(T[] value)) this.array*/ /*=*/ value;
/*if (index + 1 >= this.size)*/ {
};
/*this.padWithDefaults*/(/*+*/);
/*this.size = index*/ /*+*/ 1;
/**/

/*this.array[index]*/ /*=*/ element;
return this;
/**/

/*private Option<T[]> resizeArrayToContainIndex(int index)*/ {
};
/*int newCapacity*/ /*=*/ this.array.length;
/*if (index < newCapacity) return*/ new None<T[]>();
/*while (newCapacity <= index) newCapacity*/ /**=*/ 2;
/*=*/ (*alloc)();
/*System.arraycopy*/();
new (*Some<T[]>)();
/**/

/*private void padWithDefaults(int start, int end)*/ {
};
/*for (int i*/ /*=*/ start;
/*i*/ /*<*/ end;
/*i++) this.array[i]*/ /*=*/ this.createDefault.get();
/**/

/**/

struct StringBuffer {
};
/*private*/ /*List<Character>*/ chars;
/*public StringBuffer(List<Character> chars)*/ {
};
/*this.chars*/ /*=*/ chars;
/**/

/*public StringBuffer()*/ {
};
/*this(new ArrayList<Character>(()*/ /*->*/ '\0'));
/**/

/*public StringBuffer clear()*/ {
};
/*=*/ (*this.chars.clear)();
return this;
/**/

/*public StringBuffer append(char c)*/ {
};
/*=*/ (*this.chars.add)();
return this;
/**/

/*public String intoString()*/ {
};
/*final char[] array =*/ new char[this.chars.size()];
/*for (int i*/ /*=*/ 0;
/*<*/ (*this.chars.size)();
/*i++) array[i]*/ /*=*/ this.chars.get(i).orElse('\0');
new (*String)();
/**/

/**/

struct State {
};
/*private final*/ /*List<String>*/ segments;
/*private*/ StringBuffer buffer;
/*private*/ int depth;
/*public State()*/ {
};
new (*StringBuffer)();
new (*ArrayList<String>)();
/*this.depth*/ /*=*/ 0;
/**/

/*private Stream<String> stream()*/ {
};
return (*this.segments.stream)();
/**/

/*private State enter()*/ {
};
/*this.depth = this.depth*/ /*+*/ 1;
return this;
/**/

/*private State exit()*/ {
};
/*this.depth = this.depth*/ /*-*/ 1;
return this;
/**/

/*private boolean isShallow()*/ {
};
/*return this.depth*/ /*==*/ 1;
/**/

/*private State advance()*/ {
};
/*this.segments.add(this.buffer.intoString())*/;
/*=*/ (*this.buffer.clear)();
return this;
/**/

/*private boolean isLevel()*/ {
};
/*return this.depth*/ /*==*/ 0;
/**/

/*private State append(char c)*/ {
};
/*=*/ (*this.buffer.append)();
return this;
/**/

/**/

struct Tuple<A, B> {
	A left;
	B right;
};
/**/

/*private record Joiner(String delimiter) implements Collector<String, Option<String>>*/ {
};
/*private Joiner()*/ {
};
/*this*/();
/**/

/*@Override
		public Option<String> createInitial()*/ {
};
new (*None<String>)();
/**/

/*@Override
		public Option<String> fold(Option<String> current, String element)*/ {
};
/*return new Some<String>(switch (current)*/ {
};
/*case None<String> _*/ /*->*/ element;
/*case Some<String>(String buffer) -> buffer + this.delimiter*/ /*+*/ element;
/**/

/*)*/;
/**/

/**/

struct Streams {
};
/*public static <T> Stream<T> fromInitializedArray(T[] array)*/ {
};
/*return new Stream<T>(new*/ /*ArrayHead<T>(array,*/ array.length));
/**/

/**/

struct Header {
	String name;
	/*Option<String>*/ maybeTypeParams;
};
/*public String generate(String prefix)*/ {
};
/*final String templateString =
					this.maybeTypeParams.map(params -> "template<typename " + params + ">"*/ /*+*/ System.lineSeparator()).orElse("");
/*return templateString + prefix + " "*/ /*+*/ this.name;
/**/

/*public Option<String> generateTypeParams()*/ {
};
return (*this.maybeTypeParams.map)(/*+*/);
/**/

/**/

/*private record ListCollector<T>(Supplier<T> createDefault) implements Collector<T, List<T>>*/ {
};
/*@Override
		public List<T> createInitial()*/ {
};
new (*ArrayList<T>)();
/**/

/*@Override
		public List<T> fold(List<T> current, T element)*/ {
};
return (*current.add)();
/**/

/**/

/*private record Definition(Option<String> maybeBeforeType, String type, String name) implements JDefined*/ {
};
/*@Override
		public String generate()*/ {
};
/*final String beforeTypeString = this.maybeBeforeType().map(Main::wrap).map(value -> value +*/ /*"*/ ").orElse("");
/*return beforeTypeString + this.type() + " "*/ /*+*/ this.name();
/**/

/**/

struct Placeholder implements JDefined {
};
/*private final*/ String stripped;
/*public Placeholder(String stripped)*/ {
};
/*this.stripped*/ /*=*/ stripped;
/**/

/*@Override
		public String generate()*/ {
};
return (*wrap)();
/**/

/**/

struct EmptyHead<T> implements Head<T> {
};
/*@Override
		public Option<T> next()*/ {
};
new (*None<T>)();
/**/

/**/

struct Options {
};
/*public static <T> Stream<T> stream(Option<T> option)*/ {
};
/*return new Stream<T>(switch (option)*/ {
};
new (*EmptyHead<T>)();
new (*SingletonHead<T>)();
/**/

/*)*/;
/**/

/**/

struct SingletonHead<T> implements Head<T> {
};
/*private final*/ T value;
/*private boolean retrieved*/ /*=*/ false;
/*public SingletonHead(T value)*/ {
};
/*this.value*/ /*=*/ value;
/**/

/*@Override
		public Option<T> next()*/ {
};
/*if (this.retrieved) return*/ new None<T>();
/*this.retrieved*/ /*=*/ true;
new (*Some<T>)();
/**/

/**/

/*public static final Supplier<String> DEFAULT_STRING = ()*/ /*->*/ "";
/*public static <T> T[] alloc(int length)*/ {
};
/*//noinspection unchecked
		return (T[])*/ new Object[length];
/**/

/*public static void main(String[] args)*/ {
};
/*try*/ {
};
/*=*/ (*Paths.get)();
/*=*/ (*Files.readString)();
/*Files.writeString(source.resolveSibling("main.cpp"),*/ compile(input));
/**/

/*catch (IOException e)*/ {
};
new (*RuntimeException)();
/**/

/**/

/*private static String compile(String input)*/ {
};
return (*compileStatements)();
/**/

/*private static String compileStatements(String input, Function<String, String> mapper)*/ {
};
return (*compileAll)();
/**/

/*private static String compileAll(String input,
																	 BiFunction<State, Character, State> folder,
																	 Function<String, String> mapper)*/ {
};
/*return divide(input,*/ /*folder).map(mapper).collect(new*/ Joiner()).orElse("");
/**/

/*private static Stream<String> divide(String input, BiFunction<State, Character, State> folder)*/ {
};
new (*State)();
/*for (int i*/ /*=*/ 0;
/*<*/ (*input.length)();
/*i++)*/ {
};
/*=*/ (*input.charAt)();
/*=*/ (*folder.apply)();
/**/

return current.advance().stream();
/**/

/*private static State foldStatement(State state, char c)*/ {
};
/*=*/ (*state.append)();
/*if (c*/ /*==*/ ';
/*' && appended.isLevel())*/ return appended.advance();
/*if (c == '*/

/*' && appended.isShallow())*/ return appended.advance().exit();
/*if (c == '*/ {
};
/*')*/ return appended.enter();
/*if (c == '*/

/*')*/ return appended.exit();
return appended;
/**/
/*private static String compileRootSegment(String input)*/ {
};
/*=*/ (*input.strip)();
/*if (strip.startsWith("package ") || strip.startsWith("import "))*/ return "";
/*return compileStructure(strip).orElseGet(()*/ /*->*/ wrap(strip));
/**/
/*private static Option<String> compileStructure(String input)*/ {
};
/*if (!input.endsWith("*/
/*")) return new None<String>();*//*final String withoutEnd = input.substring(0, input.length() - "}".length());
		final int index = withoutEnd.indexOf("{");*//*if (index < 0) return new None<String>();*//*final String header = withoutEnd.substring(0, index).strip();*//*final String body = withoutEnd.substring(index + "*/ {
};
/*".length())*/;
/*=*/ (*compileStructureHeader)();
/*return new Some<String>(
				compiledHeader.left + "*/ {
};
/*" + compiledHeader.right + System.lineSeparator() + "*/

/**/;
/*" + System.lineSeparator()*/ /*+
				compileStatements(body,*/ Main::compileClassSegment));
/**/
/*private static String compileClassSegment(String input)*/ {
};
/*=*/ (*input.strip)();
/*return compileClassSegmentValue(stripped)*/ /*+*/ System.lineSeparator();
/**/
/*private static String compileClassSegmentValue(String input)*/ {
};
/*return compileStructure(input).or(() -> compileMethod(input))
																	.or(() -> compileField(input))
																	.orElseGet(()*/ /*->*/ wrap(input));
/**/
/*private static Option<String> compileField(String input)*/ {
};
/*=*/ (*input.strip)();
if (stripped.endsWith(";
/*"))*/ {
};
/*final String slice = stripped.substring(0, stripped.length()*/ /*-*/ ";
/*".length())*/;
/*return new Some<String>(parseDefined(slice).generate()*/ /*+*/ ";
/*")*/;
/**/

new (*None<String>)();
/**/
/*private static Option<String> compileMethod(String input)*/ {
};
/*final int paramEnd*/ /*=*/ input.indexOf(")");
/*if (paramEnd >= 0)*/ {
};
/*=*/ (*input.substring)();
/*final String body = input.substring(paramEnd*/ /*+*/ ")".length()).strip();
/*=*/ (*withParams.indexOf)();
/*if (paramStart >= 0)*/ {
};
/*=*/ (*withParams.substring)();
/*final String paramsString = withParams.substring(paramStart*/ /*+*/ "(".length());
/*=*/ (*body.strip)();
/*final List<JDefined> params = divide(paramsString, Main::foldValue).map(Main::parseDefined)
																																					 .collect(new ListCollector<JDefined>(() -> new*/ /*Placeholder(*/ "")));
/*final String compiledParameters =*/ /*params.stream().map(JDefined::generate).collect(new*/ Joiner()).orElse("");
if (stripped.equals(";
/*"))*/ {
};
/*final String paramTypesJoined = params.stream().map(param ->*/ {
};
/*if (param instanceof Definition definition1) return*/ new Some<String>(definition1.type);
new (*None<String>)();
/**/

/*).flatMap(Options::stream).collect(new*/ /*Joiner(",*/ ")).orElse("");
/*=*/ (*parseDefined)();
/*final*/ String generate;
/*if (jDefined instanceof Definition definition) generate = definition.type + " (*" + definition.name*/ /*+*/ ")";
/*=*/ (*jDefined.generate)();
new (*Some<String>)(/*+*/);
/*")*/;
/**/

/*else if (stripped.startsWith("*/ {
};
/*") && stripped.endsWith("*/

/*"))*/ {
};
/*final String substring = stripped.substring(1, stripped.length()*/ /*-*/ 1);
/*final String s =
							parseDefined(definitionString).generate() + "(" + compiledParameters + ")*/ {
};
/*" + wrap(substring) + "*/

/*"*/;
new (*Some<String>)();
/**/

/**/

/**/

new (*None<String>)();
/**/
/*private static String compileParameters(String params)*/ {
};
/*return compileAll(params, Main::foldValue, input*/ /*->*/ parseDefined(input).generate());
/**/
/*private static State foldValue(State state, char c)*/ {
};
/*if (c == ',')*/ return state.advance();
return (*state.append)();
/**/
/*private static JDefined parseDefined(String input)*/ {
};
/*=*/ (*input.strip)();
/*=*/ (*stripped.lastIndexOf)(/*"*/);
/*if (index >= 0)*/ {
};
/*final String beforeName =*/ /*stripped.substring(0,*/ index).strip();
/*final String name = stripped.substring(index +*/ /*"*/ ".length());
/*=*/ (*beforeName.lastIndexOf)(/*"*/);
/*if (typeSeparator >= 0)*/ {
};
/*=*/ (*beforeName.substring)();
/*final String type = beforeName.substring(typeSeparator +*/ /*"*/ ".length());
/*=*/ (*compileType)();
/*return new Definition(new Some<String>(beforeType),*/ /*newType,*/ name);
/**/

/*else return new Definition(new None<String>(),*/ /*compileType(beforeName),*/ name);
/**/

new (*Placeholder)();
/**/
/*private static String compileType(String input)*/ {
};
/*=*/ (*input.strip)();
/*if (stripped.equals("void"))*/ return "void";
/*if (isIdentifier(stripped))*/ return stripped;
return (*wrap)();
/**/
/*private static boolean isIdentifier(String input)*/ {
};
/*=*/ (*input.length)();
/*for (int index*/ /*=*/ 0;
/*index*/ /*<*/ length;
/*index++)*/ {
};
/*=*/ (*input.charAt)();
/*if (!Character.isLetter(c))*/ return false;
/**/

return true;
/**/
/*private static Tuple<String, String> compileStructureHeader(String input)*/ {
};
/*=*/ (*input.indexOf)(/*"class*/);
/*if (index >= 0)*/ {
};
/*final String name = input.substring(index +*/ /*"class*/ ".length());
/*Tuple<String,*/ (*String>)(/*+*/);
/**/

/*if (input.endsWith(")"))*/ {
};
/*final String withoutEnd = input.substring(0, input.length()*/ /*-*/ ")".length());
/*=*/ (*withoutEnd.indexOf)();
/*if (paramStart >= 0)*/ {
};
/*=*/ (*withoutEnd.substring)();
/*final String params = withoutEnd.substring(paramStart*/ /*+*/ "(".length());
/*=*/ (*beforeParams.indexOf)(/*"record*/);
/*if (keywordIndex >= 0)*/ {
};
/*final String compiledParams =
							compileAll(params, Main::foldValue, param*/ /*->*/ generateStatement(parseDefined(param).generate()));
/*final String name = beforeParams.substring(keywordIndex +*/ /*"record*/ ".length());
/*Tuple<String,*/ (*String>)(/*+*/);
/**/

/**/

/**/

/*=*/ (*input.indexOf)(/*"interface*/);
/*if (interfaceIndex >= 0)*/ {
};
/*final String afterKeyword = input.substring(interfaceIndex +*/ /*"interface*/ ".length());
String before;
/*=*/ (*afterKeyword.indexOf)(/*"permits*/);
/*if (permitsIndex >= 0)*/ {
};
/*=*/ (*afterKeyword.substring)();
/*final String[] variantsArray =
						afterKeyword.substring(permitsIndex +*/ /*"permits*/ ".length()).split(Pattern.quote(","));
/*final List<String> variants = Streams.fromInitializedArray(variantsArray)
																						 .map(String::strip)
																						*/ /*.collect(new*/ ListCollector<String>(DEFAULT_STRING));
/*=*/ (*compileNamed)();
/*final String enumName = header.name*/ /*+*/ "Tag";
/*final String enumBody =
						variants.stream().map(slice -> generateIndent() +*/ /*slice).collect(new*/ Joiner(",")).orElse("");
/*final String generatedEnum =
						"enum " + enumName + "*/ {
};
/*" + enumBody + System.lineSeparator() + "*/

/**/;
/*+*/ (*System.lineSeparator)();
/*final String typeParamsString*/ /*=*/ header.generateTypeParams().orElse("");
/*final String unionBody = variants.stream()
																				 .map(variant -> generateStatement(
																						 variant + typeParamsString + " " + variant.toLowerCase(Locale.ROOT)))
																				 .collect(new*/ /*Joiner())*/ .orElse("");
/*final String unionName = header.generate("union")*/ /*+*/ "Data";
/*final String generatedUnion =
						unionName + "*/ {
};
/*" + unionBody + System.lineSeparator() + "*/

/**/;
/*+*/ (*System.lineSeparator)();
/*+*/ (*header.generate)();
/*return new Tuple<String, String>(before,
																				 generateStatement(enumName + " tag") + generateStatement(
																						 header.name + "Data" + header.generateTypeParams().orElse("") +*/ /*"*/ data"));
/**/

/*else*/ {
};
/*=*/ (*compileNamed)();
/*=*/ (*header.generate)();
/*Tuple<String,*/ (*String>)();
/**/

/**/

/*return new Tuple<String,*/ /*String>(wrap(input),*/ "");
/**/
/*private static String generateStatement(String content)*/ {
};
/*return generateIndent() + content*/ /*+*/ ";
/*"*/;
/**/
/*private static String generateIndent()*/ {
};
/*return System.lineSeparator()*/ /*+*/ "\t";
/**/
/*private static Header compileNamed(String input)*/ {
};
/*=*/ (*input.strip)();
/*if (!stripped.endsWith(">")) return new Header(stripped,*/ new None<String>());
/*final String withoutEnd = stripped.substring(0, stripped.length()*/ /*-*/ 1);
/*=*/ (*withoutEnd.indexOf)();
/*if (paramStart < 0) return new Header(stripped,*/ new None<String>());
/*=*/ (*withoutEnd.substring)();
/*final String typeParameters = withoutEnd.substring(paramStart*/ /*+*/ "<".length());
/*return new Header(name,*/ new Some<String>(typeParameters));
/**/
/*private static String wrap(String input)*/ {
};
/*return "start" + input.replace("start", "start").replace("end", "end")*/ /*+*/ "*/";
/**/
/*}*/