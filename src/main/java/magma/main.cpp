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
/*T*/ orElse(/*T*/ other);
/*T*/ orElseGet(/*Supplier<T>*/ other);
/*Option<T>*/ or(/*Supplier<Option<T>>*/ other);
/*<R>*/ /*Option<R>*/ map(/*Function<T*//*R>*/ mapper);
/**/

template<typename T>
struct Head {
};
/*Option<T>*/ next(/**/);
/**/

template<typename T>
struct List {
};
/*List<T>*/ clear(/**/);
/*List<T>*/ add(/*T*/ element);
/*int*/ size(/**/);
/*Option<T>*/ get(/*int*/ index);
/*Stream<T>*/ stream(/**/);
/**/

template<typename T, C>
struct Collector {
};
/*C*/ createInitial(/**/);
/*C*/ fold(/*C*/ current/*T*/ element);
/**/

/*public record Some<T>(T value) implements Option<T>*/ {
};
/*@Override
		public T orElse(T other)*/ {
};
/*return*/ this.value;
/**/

/*@Override
		public T orElseGet(Supplier<T> other)*/ {
};
/*return*/ this.value;
/**/

/*@Override
		public Option<T> or(Supplier<Option<T>> other)*/ {
};
/*return*/ this;
/**/

/*@Override
		public <R> Option<R> map(Function<T, R> mapper)*/ {
};
/*return*/ /*new*/ Some<R>(/*mapper.apply(this.value*/)/*);*/
/**/

/**/

struct None<T> implements Option<T> {
};
/*@Override
		public T orElse(T other)*/ {
};
/*return*/ other;
/**/

/*@Override
		public T orElseGet(Supplier<T> other)*/ {
};
/*return*/ other.get(/**/);
/**/

/*@Override
		public Option<T> or(Supplier<Option<T>> other)*/ {
};
/*return*/ other.get(/**/);
/**/

/*@Override
		public <R> Option<R> map(Function<T, R> mapper)*/ {
};
/*return*/ /*new*/ None<R>(/**/);
/**/

/**/

struct MapStream<S, T> {
	/*Head<S>*/ head;
	/*Function<S*/;
	/*T>*/ mapper;
};
/*public Option<T> next()*/ {
};
/*return switch (this.head.next())*/ {
};
/*case None<S> _ ->*/ /*new*/ None<T>(/**/);
/*case Some<S> v ->*/ /*new*/ Some<T>(/*this.mapper.apply(v.value*/)/*);*/
/**/

/**/;
/**/

/*public <C> C collect(Collector<T, C> collector)*/ {
};
/*return*/ this.fold(/*collector.createInitial(*/)/*, collector::fold);*/
/**/

/*public <C> C fold(C initial, BiFunction<C, T, C> folder)*/ {
};
/*while*/(/*true*/)/*if (this.next() instanceof Some<T>(T value)) initial = folder.apply(initial, value);*/
/*else*/ /*return*/ initial;
/**/

/*public <S> MapStream<T, S> map(Function<T, S> mapper)*/ {
};
/*return new*/ /*MapStream<T,*/ S>(/*this::next*//*mapper*/);
/**/

/**/

struct Stream<T> {
	/*Head<T>*/ head;
};
/*public <R> MapStream<T, R> map(Function<T, R> mapper)*/ {
};
/*return new*/ /*MapStream<T,*/ R>(/*this.head*//*mapper*/);
/**/

/**/

struct ArrayHead<T> implements Head<T> {
};
/*private final*/ /*T[]*/ array;
/*private final*/ /*int*/ size;
/*private int counter*/ /*=*/ 0;
/*private ArrayHead(T[] array, int size)*/ {
};
/*this.array*/ /*=*/ array;
/*this.size*/ /*=*/ size;
/**/

/*@Override
		public Option<T> next()*/ {
};
/*if*/(/*this.counter*/ /*>=*/ this.size)/*return new None<T>();*/
/*final T next*/ /*=*/ this.array[this.counter];
/*this.counter++*/;
/*return*/ /*new*/ Some<T>(/*next*/);
/**/

/**/

struct ArrayList<T> implements List<T> {
};
/*private final*/ /*Supplier<T>*/ createDefault;
/*private*/ /*T[]*/ array;
/*private*/ /*int*/ size;
/*public ArrayList(T[] array, Supplier<T> createDefault)*/ {
};
/*this.array*/ /*=*/ array;
/*this.createDefault*/ /*=*/ createDefault;
/*this.size*/ /*=*/ 0;
/**/

/*public ArrayList(Supplier<T> createDefault)*/ {
};
/*this*/(/*alloc(10*/)/*, createDefault);*/
/**/

/*@Override
		public List<T> clear()*/ {
};
/*this.size*/ /*=*/ 0;
/*return*/ this;
/**/

/*@Override
		public List<T> add(T element)*/ {
};
/*return*/ this.set(/*this.size*//*element*/);
/**/

/*@Override
		public int size()*/ {
};
/*return*/ this.size;
/**/

/*@Override
		public Option<T> get(int index)*/ {
};
/*if*/(/*index*/ /*<*/ this.size)/*return new Some<T>(this.array[index]);*/
/*else return*/ /*new*/ None<T>(/**/);
/**/

/*@Override
		public Stream<T> stream()*/ {
};
/*return*/ /*new*/ Stream<T>(/*new*/ ArrayHead<T>(this.array/*this.size*/)/*);*/
/**/

/*private List<T> set(int index, T element)*/ {
};
/*Option<T[]> option*/ /*=*/ this.resizeArrayToContainIndex(/*index*/);
/*if*/(/*option instanceof*/ /*Some<T[]>(T[]*/ value)/*) this.array = value;*/
/*if (index + 1 >= this.size)*/ {
};
/*this.padWithDefaults*/(/*this.size*//*index*/ /*+*/ 1);
/*this.size = index*/ /*+*/ 1;
/**/

/*this.array[index]*/ /*=*/ element;
/*return*/ this;
/**/

/*private Option<T[]> resizeArrayToContainIndex(int index)*/ {
};
/*int newCapacity*/ /*=*/ this.array.length;
/*if*/(/*index*/ /*<*/ newCapacity)/*return new None<T[]>();*/
/*while*/(/*newCapacity*/ /*<=*/ index)/*newCapacity *= 2;*/
/*final T[] newArray*/ /*=*/ alloc(/*newCapacity*/);
/*System.arraycopy*/(/*this.array*//*0*//*newArray*//*0*//*this.size*/);
/*return*/ /*new*/ Some<T[]>(/*newArray*/);
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
/*this*/(/*new*/ ArrayList<Character>(()/*-> '\0'));*/
/**/

/*public StringBuffer clear()*/ {
};
/*this.chars*/ /*=*/ this.chars.clear(/**/);
/*return*/ this;
/**/

/*public StringBuffer append(char c)*/ {
};
/*this.chars*/ /*=*/ this.chars.add(/*c*/);
/*return*/ this;
/**/

/*public String intoString()*/ {
};
/*final char[] array =*/ /*new*/ char[this.chars.size(/**/)/*];*/
/*for (int i*/ /*=*/ 0;
/*i*/ /*<*/ this.chars.size(/**/);
/*i++) array[i]*/ /*=*/ this.chars.get(i).orElse('\0');
/*return*/ /*new*/ String(/*array*/);
/**/

/**/

struct State {
};
/*private final*/ /*List<String>*/ segments;
/*private*/ /*StringBuffer*/ buffer;
/*private*/ /*int*/ depth;
/*public State()*/ {
};
/*this.buffer =*/ /*new*/ StringBuffer(/**/);
/*this.segments =*/ /*new*/ ArrayList<String>(/*DEFAULT_STRING*/);
/*this.depth*/ /*=*/ 0;
/**/

/*private Stream<String> stream()*/ {
};
/*return*/ this.segments.stream(/**/);
/**/

/*private State enter()*/ {
};
/*this.depth = this.depth*/ /*+*/ 1;
/*return*/ this;
/**/

/*private State exit()*/ {
};
/*this.depth = this.depth*/ /*-*/ 1;
/*return*/ this;
/**/

/*private boolean isShallow()*/ {
};
/*return this.depth*/ /*==*/ 1;
/**/

/*private State advance()*/ {
};
/*this.segments.add*/(/*this.buffer.intoString(*/)/*);*/
/*this.buffer*/ /*=*/ this.buffer.clear(/**/);
/*return*/ this;
/**/

/*private boolean isLevel()*/ {
};
/*return this.depth*/ /*==*/ 0;
/**/

/*private State append(char c)*/ {
};
/*this.buffer*/ /*=*/ this.buffer.append(/*c*/);
/*return*/ this;
/**/

/**/

struct Tuple<A, B> {
	/*A*/ left;
	/*B*/ right;
};
/**/

/*private record Joiner(String delimiter) implements Collector<String, Option<String>>*/ {
};
/*private Joiner()*/ {
};
/*this*/(/*""*/);
/**/

/*@Override
		public Option<String> createInitial()*/ {
};
/*return*/ /*new*/ None<String>(/**/);
/**/

/*@Override
		public Option<String> fold(Option<String> current, String element)*/ {
};
/*return new Some<String>(switch (current)*/ {
};
/*case None<String> _*/ /*->*/ element;
/*case*/ Some<String>(/*String*/ buffer)/*-> buffer + this.delimiter + element;*/
/**/

/*)*/;
/**/

/**/

struct Streams {
};
/*public static <T> Stream<T> fromInitializedArray(T[] array)*/ {
};
/*return*/ /*new*/ Stream<T>(/*new*/ ArrayHead<T>(array/*array.length*/)/*);*/
/**/

/**/

struct Header {
	/*String*/ name;
	/*Option<String>*/ maybeTypeParams;
};
/*public String generate(String prefix)*/ {
};
/*final String*/ /*templateString*/ =
					this.maybeTypeParams.map(/*params -> "template<typename " + params + ">"*/ /*+*/ System.lineSeparator()/*).orElse("");*/
/*return templateString + prefix + " "*/ /*+*/ this.name;
/**/

/*public Option<String> generateTypeParams()*/ {
};
/*return*/ this.maybeTypeParams.map(/*typeParams -> "<" + typeParams*/ /*+*/ ">");
/**/

/**/

/*private record ListCollector<T>(Supplier<T> createDefault) implements Collector<T, List<T>>*/ {
};
/*@Override
		public List<T> createInitial()*/ {
};
/*return*/ /*new*/ ArrayList<>(/*this.createDefault*/);
/**/

/*@Override
		public List<T> fold(List<T> current, T element)*/ {
};
/*return*/ current.add(/*element*/);
/**/

/**/

/*public static final Supplier<String>*/ /*DEFAULT_STRING*/ =(/**/)/*-> "";*/
/*public static <T> T[] alloc(int length)*/ {
};
/*//noinspection*/ unchecked
		return(/*T[]*/)/*new Object[length];*/
/**/

/*public static void main(String[] args)*/ {
};
/*try*/ {
};
/*final Path source*/ /*=*/ Paths.get(/*"."*//*"src"*//*"main"*//*"java"*//*"magma"*//*"Main.java"*/);
/*final String input*/ /*=*/ Files.readString(/*source*/);
/*Files.writeString*/(/*source.resolveSibling("main.cpp"*/)/*, compile(input));*/
/**/

/*catch (IOException e)*/ {
};
/*throw*/ /*new*/ RuntimeException(/*e*/);
/**/

/**/

/*private static String compile(String input)*/ {
};
/*return*/ compileStatements(/*input*//*Main::compileRootSegment*/);
/**/

/*private static String compileStatements(String input, Function<String, String> mapper)*/ {
};
/*return*/ compileAll(/*input*//*Main::foldStatement*//*mapper*/);
/**/

/*private static String compileAll(String input,
																	 BiFunction<State, Character, State> folder,
																	 Function<String, String> mapper)*/ {
};
/*return*/ divide(/*input*//*folder*/)/*.map(mapper).collect(new Joiner()).orElse("");*/
/**/

/*private static Stream<String> divide(String input, BiFunction<State, Character, State> folder)*/ {
};
/*State current =*/ /*new*/ State(/**/);
/*for (int i*/ /*=*/ 0;
/*i*/ /*<*/ input.length(/**/);
/*i++)*/ {
};
/*final char c*/ /*=*/ input.charAt(/*i*/);
/*current*/ /*=*/ folder.apply(/*current*//*c*/);
/**/

/*return*/ current.advance(/**/)/*.stream();*/
/**/

/*private static State foldStatement(State state, char c)*/ {
};
/*final State appended*/ /*=*/ state.append(/*c*/);
/*if (c*/ /*==*/ ';
/*'*/ /*&&*/ appended.isLevel(/**/)/*) return appended.advance();*/
/*if (c == '*/

/*'*/ /*&&*/ appended.isShallow(/**/)/*) return appended.advance().exit();*/
/*if (c == '*/ {
};
/*')*/ /*return*/ appended.enter();
/*if (c == '*/

/*')*/ /*return*/ appended.exit();
/*return*/ appended;
/**/
/*private static String compileRootSegment(String input)*/ {
};
/*final String strip*/ /*=*/ input.strip(/**/);
/*if*/(/*strip.startsWith("package*/ ")/*|| strip.startsWith("import ")) return "";*/
/*return*/ compileStructure(/*strip*/)/*.orElseGet(() -> wrap(strip));*/
/**/
/*private static Option<String> compileStructure(String input)*/ {
};
/*if (!input.endsWith("*/
/*")) return new None<String>();*//*final String withoutEnd = input.substring(0, input.length() - "}".length());
		final int index = withoutEnd.indexOf("{");*//*if (index < 0) return new None<String>();*//*final String header = withoutEnd.substring(0, index).strip();*//*final String body = withoutEnd.substring(index + "*/ {
};
/*".length*/(/**/)/*);*/
/*final Tuple<String, String> compiledHeader*/ /*=*/ compileStructureHeader(/*header*/);
/*return new Some<String>(
				compiledHeader.left + "*/ {
};
/*" + compiledHeader.right*/ /*+*/ System.lineSeparator(/**/)/*+ "*/

/**/;
/*"*/ /*+*/ System.lineSeparator(/**/)/*+
				compileStatements(body, Main::compileClassSegment));*/
/**/
/*private static String compileClassSegment(String input)*/ {
};
/*final String stripped*/ /*=*/ input.strip(/**/);
/*return*/ compileClassSegmentValue(/*stripped*/)/*+ System.lineSeparator();*/
/**/
/*private static String compileClassSegmentValue(String input)*/ {
};
/*return*/ compileStructure(/*input*/)/*.or(() -> compileMethod(input))
																	.or(() -> compileField(input))
																	.orElseGet(() -> wrap(input));*/
/**/
/*private static Option<String> compileField(String input)*/ {
};
/*final String stripped*/ /*=*/ input.strip(/**/);
/*if*/ (stripped.endsWith(";
/*"))*/ {
};
/*final String slice*/ /*=*/ stripped.substring(/*0*//*stripped.length(*/)/*- ";*/
/*".length*/(/**/)/*);*/
/*return*/ /*new*/ Some<String>(/*compileDefinition(slice*/)/*+ ";*/
/*")*/;
/**/

/*else return*/ /*new*/ None<String>(/**/);
/**/
/*private static Option<String> compileMethod(String input)*/ {
};
/*final int paramEnd*/ /*=*/ input.indexOf(/*"*/)/*");*/
/*if (paramEnd >= 0)*/ {
};
/*final String withParams*/ /*=*/ input.substring(/*0*//*paramEnd*/);
/*final String body*/ /*=*/ input.substring(/*paramEnd*/ /*+*/ ")/*".length()).strip();*/
/*final int paramStart*/ /*=*/ withParams.indexOf(/*"("*/);
/*if (paramStart >= 0)*/ {
};
/*final String definition*/ /*=*/ withParams.substring(/*0*//*paramStart*/);
/*final String params*/ /*=*/ withParams.substring(/*paramStart*/ /*+*/ "(".length()/*);*/
/*return*/ /*new*/ Some<String>(/*compileDefinition(definition*/)/*+ "(" + compileParameters(params) + ")" + compileMethodBody(body));*/
/**/

/**/

/*return*/ /*new*/ None<String>(/**/);
/**/
/*private static String compileMethodBody(String body)*/ {
};
/*final String stripped*/ /*=*/ body.strip(/**/);
/*if*/ (stripped.equals(";
/*"))*/ /*return*/ ";
/*"*/;
/*if (stripped.startsWith("*/ {
};
/*") && stripped.endsWith("*/

/*"))*/ {
};
/*final String substring*/ /*=*/ stripped.substring(/*1*//*stripped.length(*/)/*- 1);*/
/*return "*/ {
};
/*"*/ /*+*/ wrap(/*substring*/)/*+ "*/

/*"*/;
/**/

/*return*/ wrap(/*body*/);
/**/
/*private static String compileParameters(String params)*/ {
};
/*return*/ compileAll(/*params*//*Main::foldValue*//*Main::compileDefinition*/);
/**/
/*private static State foldValue(State state, char c)*/ {
};
/*if*/(/*c*/ /*==*/ '/*'*/)/*return state.advance();*/
/*else*/ /*return*/ state.append(/*c*/);
/**/
/*private static String compileDefinition(String input)*/ {
};
/*final String stripped*/ /*=*/ input.strip(/**/);
/*final int index*/ /*=*/ stripped.lastIndexOf(/*"*/ ");
/*if (index >= 0)*/ {
};
/*final String beforeName*/ /*=*/ stripped.substring(/*0*//*index*/)/*.strip();*/
/*final String name*/ /*=*/ stripped.substring(/*index +*/ /*"*/ ".length()/*);*/
/*final int typeSeparator*/ /*=*/ beforeName.lastIndexOf(/*"*/ ");
/*if (typeSeparator >= 0)*/ {
};
/*final String beforeType*/ /*=*/ beforeName.substring(/*0*//*typeSeparator*/);
/*final String type*/ /*=*/ beforeName.substring(/*typeSeparator +*/ /*"*/ ".length()/*);*/
/*return*/ wrap(/*beforeType*/)/*+ " " + compileType(type) + " " + name;*/
/**/

/*else*/ /*return*/ compileType(/*beforeName*/)/*+ " " + name;*/
/**/

/*return*/ wrap(/*stripped*/);
/**/
/*private static String compileType(String input)*/ {
};
/*final String stripped*/ /*=*/ input.strip(/**/);
/*if*/(/*stripped.equals("void"*/)/*) return "void";*/
/*return*/ wrap(/*stripped*/);
/**/
/*private static Tuple<String, String> compileStructureHeader(String input)*/ {
};
/*final int index*/ /*=*/ input.indexOf(/*"class*/ ");
/*if (index >= 0)*/ {
};
/*final String name*/ /*=*/ input.substring(/*index +*/ /*"class*/ ".length()/*);*/
/*return new*/ /*Tuple<String,*/ String>(/*"struct "*/ /*+*/ name/*""*/);
/**/

/*if (input.endsWith(")"))*/ {
};
/*final String withoutEnd*/ /*=*/ input.substring(/*0*//*input.length(*/)/*- ")".length());*/
/*final int paramStart*/ /*=*/ withoutEnd.indexOf(/*"("*/);
/*if (paramStart >= 0)*/ {
};
/*final String beforeParams*/ /*=*/ withoutEnd.substring(/*0*//*paramStart*/);
/*final String params*/ /*=*/ withoutEnd.substring(/*paramStart*/ /*+*/ "(".length()/*);*/
/*final int keywordIndex*/ /*=*/ beforeParams.indexOf(/*"record*/ ");
/*if (keywordIndex >= 0)*/ {
};
/*final String*/ /*compiledParams*/ =
							compileAll(/*params*//*Main::foldValue*//*param*/ /*->*/ generateStatement(compileDefinition(param)/*));*/
/*final String name*/ /*=*/ beforeParams.substring(/*keywordIndex +*/ /*"record*/ ".length()/*);*/
/*return new*/ /*Tuple<String,*/ String>(/*"struct "*/ /*+*/ name/*compiledParams*/);
/**/

/**/

/**/

/*final int interfaceIndex*/ /*=*/ input.indexOf(/*"interface*/ ");
/*if (interfaceIndex >= 0)*/ {
};
/*final String afterKeyword*/ /*=*/ input.substring(/*interfaceIndex +*/ /*"interface*/ ".length()/*);*/
/*String*/ before;
/*final int permitsIndex*/ /*=*/ afterKeyword.indexOf(/*"permits*/ ");
/*if (permitsIndex >= 0)*/ {
};
/*final String beforePermits*/ /*=*/ afterKeyword.substring(/*0*//*permitsIndex*/);
/*final String[]*/ /*variantsArray*/ =
						afterKeyword.substring(/*permitsIndex +*/ /*"permits*/ ".length()/*).split(Pattern.quote(","));*/
/*final List<String>*/ /*variants*/ =
						Streams.fromInitializedArray(/*variantsArray*/)/*.map(String::strip).collect(new ListCollector<>(DEFAULT_STRING));*/
/*final Header header*/ /*=*/ compileNamed(/*beforePermits*/);
/*final String enumName = header.name*/ /*+*/ "Tag";
/*final String*/ /*enumBody*/ =
						variants.stream(/**/)/*.map(slice -> generateIndent() + slice).collect(new Joiner(",")).orElse("");*/
/*final String generatedEnum =
						"enum " + enumName + "*/ {
};
/*" + enumBody*/ /*+*/ System.lineSeparator(/**/)/*+ "*/

/**/;
/*"*/ /*+*/ System.lineSeparator(/**/);
/*final String typeParamsString*/ /*=*/ header.generateTypeParams(/**/)/*.orElse("");*/
/*final String unionBody*/ /*=*/ variants.stream(/**/)/*.map(variant -> generateStatement(
																						 variant + typeParamsString + " " + variant.toLowerCase(Locale.ROOT)))
																				 .collect(new Joiner())
																				 .orElse("");*/
/*final String unionName*/ /*=*/ header.generate(/*"union"*/)/*+ "Data";*/
/*final String generatedUnion =
						unionName + "*/ {
};
/*" + unionBody*/ /*+*/ System.lineSeparator(/**/)/*+ "*/

/**/;
/*"*/ /*+*/ System.lineSeparator(/**/);
/*before = generatedEnum + generatedUnion*/ /*+*/ header.generate(/*"struct"*/);
/*return new*/ /*Tuple<String,*/ String>(/*before*//*generateStatement(enumName +*/ /*"*/ tag")/*+
																				 generateStatement(header.name + "Data" + header.generateTypeParams().orElse("") + " data"));*/
/**/

/*else*/ {
};
/*final Header header*/ /*=*/ compileNamed(/*afterKeyword*/);
/*before*/ /*=*/ header.generate(/*"struct"*/);
/*return new*/ /*Tuple<String,*/ String>(/*before*//*""*/);
/**/

/**/

/*return new*/ /*Tuple<String,*/ String>(/*wrap(input*/)/*, "");*/
/**/
/*private static String generateStatement(String content)*/ {
};
/*return*/ generateIndent(/**/)/*+ content + ";*/
/*"*/;
/**/
/*private static String generateIndent()*/ {
};
/*return*/ System.lineSeparator(/**/)/*+ "\t";*/
/**/
/*private static Header compileNamed(String input)*/ {
};
/*final String stripped*/ /*=*/ input.strip(/**/);
/*if*/(/*!stripped.endsWith(">"*/)/*) return new Header(stripped, new None<String>());*/
/*final String withoutEnd*/ /*=*/ stripped.substring(/*0*//*stripped.length(*/)/*- 1);*/
/*final int paramStart*/ /*=*/ withoutEnd.indexOf(/*"<"*/);
/*if*/(/*paramStart*/ /*<*/ 0)/*return new Header(stripped, new None<String>());*/
/*final String name*/ /*=*/ withoutEnd.substring(/*0*//*paramStart*/);
/*final String typeParameters*/ /*=*/ withoutEnd.substring(/*paramStart*/ /*+*/ "<".length()/*);*/
/*return*/ /*new*/ Header(/*name*//*new*/ Some<String>(typeParameters)/*);*/
/**/
/*private static String wrap(String input)*/ {
};
/*return "start"*/ /*+*/ input.replace(/*"start"*//*"start"*/)/*.replace("end", "end") + "end";*/
/**/
/*}*/