struct Main {
};
struct Head<T> {
};
/*Optional<T>*/ next();
/**/

struct List<T> {
};
/*List<T>*/ clear();
/*List<T>*/ /*add(T*/ element);
/*int*/ size();
/*Optional<T>*/ /*get(int*/ index);
/*Stream<T>*/ stream();
/**/

struct Collector<T, C> {
};
/*C*/ createInitial();
/*C fold(C current,*/ /*T*/ element);
/**/

struct MapStream<T> {
	/*Head<T>*/ next;
};
/*public <C> C collect(Collector<T, C> collector)*/ {
};
/*return*/ /*this.fold(collector.createInitial(),*/ collector::fold);
/**/

/*public <C> C fold(C initial, BiFunction<C, T, C> folder)*/ {
};
/*while (true)*/ {
};
/*final Optional<T> next*/ /*=*/ this.next.next();
/*if (next.isPresent()) initial =*/ /*folder.apply(initial,*/ next.get());
/*else*/ /*return*/ initial;
/**/

/**/

/**/

struct Stream<T> {
	/*Head<T>*/ head;
};
/*public <R> MapStream<R> map(Function<T, R> mapper)*/ {
};
/*return new MapStream<R>(()*/ /*->*/ this.head.next().map(mapper));
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
		public Optional<T> next()*/ {
};
/*if (this.counter >= this.size)*/ /*return*/ Optional.empty();
/*final T next*/ /*=*/ this.array[this.counter];
/*this.counter++*/;
/*return*/ Optional.of(next);
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
/*this(alloc(10),*/ createDefault);
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
/*return*/ /*this.set(this.size,*/ element);
/**/

/*@Override
		public int size()*/ {
};
/*return*/ this.size;
/**/

/*@Override
		public Optional<T> get(int index)*/ {
};
/*if (index < this.size)*/ /*return*/ Optional.of(this.array[index]);
/*else*/ /*return*/ Optional.empty();
/**/

/*@Override
		public Stream<T> stream()*/ {
};
/*return new Stream<T>(new*/ /*ArrayHead<T>(this.array,*/ this.size));
/**/

/*private List<T> set(int index, T element)*/ {
};
/*this.resizeArrayToContainIndex(index).ifPresent(newArray -> this.array*/ /*=*/ newArray);
/*if (index + 1 >= this.size)*/ {
};
/*this.padWithDefaults(this.size, index*/ /*+*/ 1);
/*this.size = index*/ /*+*/ 1;
/**/

/*this.array[index]*/ /*=*/ element;
/*return*/ this;
/**/

/*private Optional<T[]> resizeArrayToContainIndex(int index)*/ {
};
/*int newCapacity*/ /*=*/ this.array.length;
/*if (index < newCapacity)*/ /*return*/ Optional.empty();
/*while (newCapacity <= index) newCapacity*/ /**=*/ 2;
/*final T[] newArray*/ /*=*/ alloc(newCapacity);
/*System.arraycopy(this.array, 0, newArray,*/ /*0,*/ this.size);
/*return*/ Optional.of(newArray);
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
/*this.chars*/ /*=*/ this.chars.clear();
/*return*/ this;
/**/

/*public StringBuffer append(char c)*/ {
};
/*this.chars*/ /*=*/ this.chars.add(c);
/*return*/ this;
/**/

/*public String intoString()*/ {
};
/*final char[] array =*/ /*new*/ char[this.chars.size()];
/*for (int i*/ /*=*/ 0;
/*i*/ /*<*/ this.chars.size();
/*i++) array[i]*/ /*=*/ this.chars.get(i).orElse('\0');
/*return*/ /*new*/ String(array);
/**/

/**/

struct State {
};
/*private final*/ /*List<String>*/ segments;
/*private*/ /*StringBuffer*/ buffer;
/*private*/ /*int*/ depth;
/*public State()*/ {
};
/*this.buffer =*/ /*new*/ StringBuffer();
/*this.segments = new ArrayList<String>(()*/ /*->*/ "");
/*this.depth*/ /*=*/ 0;
/**/

/*private Stream<String> stream()*/ {
};
/*return*/ this.segments.stream();
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
/*this.segments.add(this.buffer.intoString())*/;
/*this.buffer*/ /*=*/ this.buffer.clear();
/*return*/ this;
/**/

/*private boolean isLevel()*/ {
};
/*return this.depth*/ /*==*/ 0;
/**/

/*private State append(char c)*/ {
};
/*this.buffer*/ /*=*/ this.buffer.append(c);
/*return*/ this;
/**/

/**/

struct Tuple<A, B> {
	/*A*/ left;
	/*B*/ right;
};
/**/

struct Joiner implements Collector<String, Optional<String>> {
};
/*@Override
		public Optional<String> createInitial()*/ {
};
/*return*/ Optional.empty();
/**/

/*@Override
		public Optional<String> fold(Optional<String> current, String element)*/ {
};
/*return Optional.of(current.map(s -> s*/ /*+*/ element).orElse(element));
/**/

/**/

/*public static <T> T[] alloc(int length)*/ {
};
/*//noinspection unchecked
		return (T[])*/ /*new*/ Object[length];
/**/

/*public static void main(String[] args)*/ {
};
/*try*/ {
};
/*final Path source = Paths.get(".", "src", "main", "java",*/ /*"magma",*/ "Main.java");
/*final String input*/ /*=*/ Files.readString(source);
/*Files.writeString(source.resolveSibling("main.c"),*/ compile(input));
/**/

/*catch (IOException e)*/ {
};
/*throw*/ /*new*/ RuntimeException(e);
/**/

/**/

/*private static String compile(String input)*/ {
};
/*return*/ /*compileStatements(input,*/ Main::compileRootSegment);
/**/

/*private static String compileStatements(String input, Function<String, String> mapper)*/ {
};
/*return compileAll(input,*/ /*Main::foldStatement,*/ mapper);
/**/

/*private static String compileAll(String input,
																	 BiFunction<State, Character, State> folder,
																	 Function<String, String> mapper)*/ {
};
/*return divide(input,*/ /*folder).map(mapper).collect(new*/ Joiner()).orElse("");
/**/

/*private static Stream<String> divide(String input, BiFunction<State, Character, State> folder)*/ {
};
/*State current =*/ /*new*/ State();
/*for (int i*/ /*=*/ 0;
/*i*/ /*<*/ input.length();
/*i++)*/ {
};
/*final char c*/ /*=*/ input.charAt(i);
/*current =*/ /*folder.apply(current,*/ c);
/**/

/*return*/ current.advance().stream();
/**/

/*private static State foldStatement(State state, char c)*/ {
};
/*final State appended*/ /*=*/ state.append(c);
/*if (c*/ /*==*/ ';
/*' && appended.isLevel())*/ /*return*/ appended.advance();
/*if (c == '*/

/*' && appended.isShallow())*/ /*return*/ appended.advance().exit();
/*if (c == '*/ {
};
/*')*/ /*return*/ appended.enter();
/*if (c == '*/

/*')*/ /*return*/ appended.exit();
/*return*/ appended;
/**/
/*private static String compileRootSegment(String input)*/ {
};
/*final String strip*/ /*=*/ input.strip();
/*if (strip.startsWith("package ") || strip.startsWith("import "))*/ /*return*/ "";
/*return compileStructure(strip).orElseGet(()*/ /*->*/ wrap(strip));
/**/
/*private static Optional<String> compileStructure(String input)*/ {
};
/*if (!input.endsWith("*/
/*")) return Optional.empty();*//*final String withoutEnd = input.substring(0, input.length() - "}".length());
		final int index = withoutEnd.indexOf("{");*//*if (index < 0) return Optional.empty();*//*final String header = withoutEnd.substring(0, index).strip();*//*final String body = withoutEnd.substring(index + "*/ {
};
/*".length())*/;
/*final Tuple<String, String> compiledHeader*/ /*=*/ compileStructureHeader(header);
/*return Optional.of(
				compiledHeader.left + "*/ {
};
/*" + compiledHeader.right + System.lineSeparator() + "*/

/**/;
/*" + System.lineSeparator()*/ /*+
				compileStatements(body,*/ Main::compileClassSegment));
/**/
/*private static String compileClassSegment(String input)*/ {
};
/*final String stripped*/ /*=*/ input.strip();
/*return compileClassSegmentValue(stripped)*/ /*+*/ System.lineSeparator();
/**/
/*private static String compileClassSegmentValue(String input)*/ {
};
/*return compileStructure(input).or(() -> compileMethod(input))
																	.or(() -> compileField(input))
																	.orElseGet(()*/ /*->*/ wrap(input));
/**/
/*private static Optional<? extends String> compileField(String input)*/ {
};
/*final String stripped*/ /*=*/ input.strip();
/*if*/ (stripped.endsWith(";
/*"))*/ {
};
/*final String slice = stripped.substring(0, stripped.length()*/ /*-*/ ";
/*".length())*/;
/*return Optional.of(compileDefinition(slice)*/ /*+*/ ";
/*")*/;
/**/

/*else*/ /*return*/ Optional.empty();
/**/
/*private static Optional<String> compileMethod(String input)*/ {
};
/*if (!input.endsWith("*/
/*")) return Optional.empty();*//*final String withoutEnd = input.substring(0, input.length() - "}".length());

		final int index = withoutEnd.indexOf("{");*//*if (index < 0) return Optional.empty();*//*final String header = withoutEnd.substring(0, index).strip();*//*final String body = withoutEnd.substring(index + "*/ {
};
/*".length())*/;
/*if (!header.endsWith(")"))*/ /*return*/ Optional.empty();
/*final String headerWithoutEnd = header.substring(0, header.length()*/ /*-*/ ")".length());
/*final int paramStart*/ /*=*/ headerWithoutEnd.indexOf("(");
/*if (paramStart < 0)*/ /*return*/ Optional.empty();
/*final String definition =*/ /*headerWithoutEnd.substring(0,*/ paramStart);
/*final String params = headerWithoutEnd.substring(paramStart*/ /*+*/ "(".length());
/*return Optional.of(compileDefinition(definition) + "(" + compileParameters(params) + ")*/ {
};
/*" + wrap(body) + "*/

/*")*/;
/**/
/*private static String compileParameters(String params)*/ {
};
/*return compileAll(params,*/ /*Main::foldValue,*/ Main::compileDefinition);
/**/
/*private static State foldValue(State state, char c)*/ {
};
/*if (c == ',')*/ /*return*/ state.advance();
/*else*/ /*return*/ state.append(c);
/**/
/*private static String compileDefinition(String input)*/ {
};
/*final String stripped*/ /*=*/ input.strip();
/*final int index =*/ /*stripped.lastIndexOf("*/ ");
/*if (index >= 0)*/ {
};
/*final String beforeName =*/ /*stripped.substring(0,*/ index).strip();
/*final String name = stripped.substring(index +*/ /*"*/ ".length());
/*final int typeSeparator =*/ /*beforeName.lastIndexOf("*/ ");
/*if (typeSeparator >= 0)*/ {
};
/*final String beforeType =*/ /*beforeName.substring(0,*/ typeSeparator);
/*final String type = beforeName.substring(typeSeparator +*/ /*"*/ ".length());
/*return wrap(beforeType) + " " + compileType(type) + " "*/ /*+*/ name;
/**/

/*else return compileType(beforeName) + " "*/ /*+*/ name;
/**/

/*return*/ wrap(stripped);
/**/
/*private static String compileType(String input)*/ {
};
/*final String stripped*/ /*=*/ input.strip();
/*if (stripped.equals("void"))*/ /*return*/ "void";
/*return*/ wrap(stripped);
/**/
/*private static Tuple<String, String> compileStructureHeader(String input)*/ {
};
/*final int index =*/ /*input.indexOf("class*/ ");
/*if (index >= 0)*/ {
};
/*final String name = input.substring(index +*/ /*"class*/ ".length());
/*return new Tuple<String, String>("struct " +*/ /*name,*/ "");
/**/

/*if (input.endsWith(")"))*/ {
};
/*final String withoutEnd = input.substring(0, input.length()*/ /*-*/ ")".length());
/*final int paramStart*/ /*=*/ withoutEnd.indexOf("(");
/*if (paramStart >= 0)*/ {
};
/*final String beforeParams =*/ /*withoutEnd.substring(0,*/ paramStart);
/*final String params = withoutEnd.substring(paramStart*/ /*+*/ "(".length());
/*final int keywordIndex =*/ /*beforeParams.indexOf("record*/ ");
/*if (keywordIndex >= 0)*/ {
};
/*final String compiledParams = compileAll(params,
																									 Main::foldValue,
																									 param -> System.lineSeparator() + "\t" +*/ /*compileDefinition(param)*/ +
																														";
/*")*/;
/*final String name = beforeParams.substring(keywordIndex +*/ /*"record*/ ".length());
/*return new Tuple<String, String>("struct " +*/ /*name,*/ compiledParams);
/**/

/**/

/**/

/*final int interfaceIndex =*/ /*input.indexOf("interface*/ ");
/*if (interfaceIndex >= 0)*/ {
};
/*final String name = input.substring(interfaceIndex +*/ /*"interface*/ ".length());
/*return new Tuple<String, String>("struct " +*/ /*name,*/ "");
/**/

/*return new Tuple<String,*/ /*String>(wrap(input),*/ "");
/**/
/*private static String wrap(String input)*/ {
};
/*return "start" + input.replace("start", "start").replace("end", "end")*/ /*+*/ "*/";
/**/
/*}*/