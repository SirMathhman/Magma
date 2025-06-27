












class Main {
	let LINE_SEPARATOR : string = System.lineSeparator();
	constructor () {}
	main(args : string[]) : void {
		let root : any = Paths.get(".", "src", "java");/*
        Main.collect(root).match(files -> {
            final var sources = files.stream().filter(path -> path.toString().endsWith(".java")).toList();
            return Main.runWithSources(sources, root);
        }*//*, Optional::of).ifPresent(Throwable::printStackTrace);*/}
	collect(root : Path) : Result<List<Path>, IOException> {/*
        try (final var stream = Files.walk(root)) {
            return new Ok<>(stream.toList());
        }*//* catch (final IOException e) {
            return new Err<>(e);
        }*/}
	runWithSources(sources : Iterable<Path>, root : Path) : Optional<IOException> {/*
        for (final var source : sources) {
            final var maybe = Main.runWithSource(root, source);
            if (maybe.isPresent())
                return maybe;
        }*/
		return Optional.empty();}
	runWithSource(root : Path, source : Path) : Optional<IOException> {
		let relative : any = root.relativize(source.getParent());
		return Main.readString(source).match(input => Main.runWithInput(source, input, relative), /* Optional::of*/);}
	runWithInput(source : Path, input : CharSequence, relative : Path) : Optional<IOException> {
		let output : any = Main.compileRoot(input);
		let targetParent : any = Paths.get(".", "src", "node").resolve(relative);
		return Main.extracted1(targetParent).or(/*(*/) -  > Main.compileAndWrite(source, targetParent, /* output)*/);}
	compileAndWrite(source : Path, targetParent : Path, output : CharSequence) : Optional<IOException> {
		let fileName : any = source.getFileName().toString();
		let separator : any = fileName.lastIndexOf('.');
		let name : any = fileName.substring(0, separator);
		let target : any = targetParent.resolve(name + ".ts");
		return Main.writeString(target, output);}
	extracted1(targetParent : Path) : Optional<IOException> {
		if (!Files.exists(targetParent))
			return Main.createDirectories(targetParent);
		return Optional.empty();}
	writeString(path : Path, output : CharSequence) : Optional<IOException> {/*
        try {
            Files.writeString(path, output);
            return Optional.empty();
        }*//* catch (final IOException e) {
            return Optional.of(e);
        }*/}
	createDirectories(path : Path) : Optional<IOException> {/*
        try {
            Files.createDirectories(path);
            return Optional.empty();
        }*//* catch (final IOException e) {
            return Optional.of(e);
        }*/}
	readString(source : Path) : Result<string, IOException> {/*
        try {
            return new Ok<>(Files.readString(source));
        }*//* catch (final IOException e) {
            return new Err<>(e);
        }*/}
	compileRoot(input : CharSequence) : string {
		return Main.compileStatements(input, /* Main::compileRootSegment*/);}
	compileStatements(input : CharSequence, mapper : Function<string, string>) : string {
		return Main.compileAll(input, /* Main::foldStatements*/, mapper, "");}
	compileAll(input : CharSequence, folder : BiFunction<State, Character, State>, mapper : Function<string, string>, delimiter : CharSequence) : string {
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));}
	compileRootSegment(input : string) : string {
		return /*Main.compileRootSegmentValue(input.strip()) + Main.LINE_SEPARATOR*/;}
	compileRootSegmentValue(input : string) : string {
		if (input.isBlank())
			return "";
		return Main.compileNamespaced(input).or(/*(*/) -  > Main.compileStructure(/*input)*/).orElseGet(() -  > Placeholder.generate(input));}
	compileNamespaced(input : string) : Optional<string> {
		let strip : any = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import "))
			return Optional.of("");
		return Optional.empty();}
	compileStructure(input : string) : Optional<string> {
		if (input.isEmpty() || '}' != input.charAt(input.length() - 1))
			return Optional.empty();
		let withoutEnd : any = input.substring(0, /* input.length(*/) - "}".length(/*)*/);
		let contentStart : any = withoutEnd.indexOf('{');
		if (0 > contentStart)
			return Optional.empty();
		let beforeContent : any = withoutEnd.substring(0, contentStart);
		let content : any = withoutEnd.substring(contentStart + "{".length());
		let definition : any = Main.parseStructureHeader(beforeContent);/*
        final String structName;*/
		if (/*definition instanceof final StructureHeader header*/){
			if (header.annotations().contains("Actual"))
				return Optional.of("");
			structName = header.name();
		}
		let structName :  = "?";
		return Optional.of(/*definition.generate(*/) + " {" + Main.compileStatements(content, /* input1 -> Main.compileStructureSegment(input1, structName)) +
                           Main.LINE_SEPARATOR + "}"*/);}
	compileStructureSegment(input : string, structName : CharSequence) : string {
		let strip : any = input.strip();
		if (strip.isEmpty())
			return "";
		return /*Main.LINE_SEPARATOR + "\t" + Main.compileStructureSegmentValue(strip, structName)*/;}
	compileStructureSegmentValue(input : string, structName : CharSequence) : string {
		return Main.compileStatement(input, /* Main::compileAssignment*/).or(/*(*/) -  > Main.compileMethod(input, /* structName)*/).orElseGet(() -  > Placeholder.generate(input));}
	compileStatement(input : string, mapper : Function<string, Optional<string>>) : Optional<string> {
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
			return Optional.empty();
		let withoutEnd : any = input.substring(0, /* input.length(*/) - ";".length(/*)*/);
		return mapper.apply(withoutEnd).map(result => result + ";");}
	compileMethod(input : string, structName : CharSequence) : Optional<string> {
		let paramEnd : any = input.indexOf(')');
		if (0 > paramEnd)
			return Optional.empty();
		let withParams : any = input.substring(0, paramEnd);
		let paramStart : any = withParams.indexOf('(');
		if (0 > paramStart)
			return Optional.empty();
		let definition : any = withParams.substring(0, paramStart);
		let params : any = withParams.substring(paramStart + "(".length());
		let joinedParams : any = "(" + Main.compileValues(params, /* Main::compileParameter*/) + ")";
		let withBraces : any = input.substring(paramEnd + ")".length()).strip();/*
        final String outputContent;*/
		if (";".contentEquals(withBraces))
			outputContent = ";";/*
        else if (!withBraces.isEmpty() && '{' == withBraces.charAt(0) &&
                   '}' == withBraces.charAt(withBraces.length() - 1)) {
            final var substring = withBraces.substring(1, withBraces.length() - 1);
            final var compiled = Main.compileFunctionSegments(substring, 2);
            outputContent = " {" + compiled + "}";
        }*//* else
            return Optional.empty();*/
		return Optional.of(Main.parseMethodHeader(definition, structName).generateWithAfterName(joinedParams) + outputContent);}
	compileFunctionSegments(substring : string, depth : number) : string {
		return Main.compileStatements(substring, input => compileFunctionSegment(input, depth));}
	compileParameter(input : string) : string {
		if (input.isBlank())
			return "";
		return Main.parseDefinitionOrPlaceholder(input).generate();}
	compileFunctionSegment(input : string, depth : number) : string {
		if (input.isBlank())
			return "";
		return Main.compileConditional(input, depth).or(/*(*/) -  > Main.compileStatement(input, /* Main::compileFunctionStatementValue)*/).map(/*value -> System.lineSeparator(*/) + "\t".repeat(/*depth) + value*/).orElseGet(() -  > Placeholder.generate(input));}
	compileFunctionStatementValue(input : string) : Optional<string> {
		return Main.compileReturn(input).or(/*(*/) -  > Main.compileAssignment(/*input)*/);}
	compileReturn(input : string) : Optional<string> {
		let strip : any = input.strip();
		if (strip.startsWith("return ")){
			let slice : any = strip.substring("return ".length());
			return Optional.of("return " + Main.compileValueOrPlaceholder(slice));
		}
		return Optional.empty();}
	compileConditional(input : string, depth : number) : Optional<string> {
		let strip : any = input.strip();
		if (!strip.startsWith("if"))
			return Optional.empty();
		let slice : any = strip.substring("if".length()).strip();
		if (slice.isEmpty() || '(' != slice.charAt(0))
			return Optional.empty();
		let substring : any = slice.substring(1);
		return Main.divide(substring, /* Main::foldConditional*/).popFirst().flatMap(tuple => compileConditionalSegments(tuple, depth));}
	compileConditionalSegments(tuple : Tuple<string, ListLike<string>>, depth : number) : Optional<string> {
		let substring1 : any = tuple.left();
		if (substring1.isEmpty() || ')' != substring1.charAt(substring1.length() - 1))
			return Optional.empty();
		let condition : any = substring1.substring(0, substring1.length() - 1);
		let withBraces : any = tuple.right().stream().collect(Collectors.joining()).strip();/*
        final String compiled;*/
		if (withBraces.startsWith("{") && withBraces.endsWith("}")){
			let compiled1 : any = Main.compileFunctionSegments(withBraces.substring(1, withBraces.length() - 1), depth + 1);
			compiled = "{" + compiled1 + LINE_SEPARATOR + "\t".repeat(depth) + "}";
		}/* else {
            compiled = Main.compileFunctionSegment(withBraces, depth + 1);
        }*/
		return Optional.of("if (" + Main.compileValueOrPlaceholder(condition) + ")" + compiled);}
	foldConditional(state : State, c : char) : State {
		let appended : any = state.append(c);
		if ('(' == c)
			return appended.enter();
		if (')' == c){
			if (appended.isLevel())
				return appended.advance();
			return appended.exit();
		}
		return appended;}
	parseMethodHeader(input : string, structName : CharSequence) : MethodHeader {
		return Main.parseConstructor(input, structName).orElseGet(/*(*/) -  > Main.parseDefinitionOrPlaceholder(/*input)*/);}
	parseConstructor(input : string, structName : CharSequence) : Optional<MethodHeader> {
		let strip : any = input.strip();
		let index : any = strip.lastIndexOf(' ');
		if (0 <= index){
			let name : any = strip.substring(index + " ".length()).strip();
			if (name.contentEquals(structName))
				return Optional.of(new Constructor());
		}
		return Optional.empty();}
	compileAssignment(input : string) : Optional<string> {
		let separator : any = input.indexOf('=');
		if (0 <= separator){
			let before : any = input.substring(0, separator);
			let after : any = input.substring(separator + "=".length());
			let assignable : any = Main.parseDefinitionOrPlaceholder(before);/*
            final Assignable assignable1;*/
			if (/*assignable instanceof final Definition definition*/)
				assignable1 = definition.withModifier("let");
			let assignable1 :  = assignable;
			return Optional.of(/*assignable1.generate(*/) + " = " + Main.compileValueOrPlaceholder(/*after)*/);
		}
		return Optional.empty();}
	compileValueOrPlaceholder(input : string) : string {
		return Main.compileValue(input).orElseGet(/*(*/) -  > Placeholder.generate(/*input)*/);}
	compileValue(input : string) : Optional<string> {
		let maybeLambda : any = Main.compileLambda(input);
		if (maybeLambda.isPresent())
			return maybeLambda;
		let maybeOperator : any = Main.compileOperators(input);
		if (maybeOperator.isPresent())
			return maybeOperator;
		let maybeInvocation : any = Main.compileInvokable(input);
		if (maybeInvocation.isPresent())
			return maybeInvocation;
		let separator : any = input.lastIndexOf('.');
		if (0 <= separator){
			let value : any = input.substring(0, separator);
			let property : any = input.substring(separator + ".".length()).strip();
			if (Main.isSymbol(property))
				return Main.compileValue(value).map(result => result + "." + property);
		}
		let strip : any = input.strip();
		if (!strip.isEmpty() && '!' == strip.charAt(0)){
			let substring : any = strip.substring(1);
			return Main.compileValue(substring).map(value => "!" + value);
		}
		if (Main.isNumber(strip))
			return Optional.of(strip);
		if (!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == strip.charAt(strip.length() - 1))
			return Optional.of(strip);
		if (Main.isChar(strip))
			return Optional.of(strip);
		if (Main.isSymbol(strip))
			return Optional.of(strip);
		return Optional.empty();}
	compileLambda(input : string) : Optional<string> {
		let arrowIndex : any = input.indexOf(" -  > ");
		if (0 > arrowIndex)
			return Optional.empty();
		let before : any = input.substring(0, arrowIndex).strip();
		if (!Main.isSymbol(before))
			return Optional.empty();
		let after : any = input.substring(arrowIndex + " -  > ".length());/*
        return Main.compileValue(after).map(afterResult -> {
            return before + " => " + afterResult;
        }*//*);*/}
	compileOperators(input : string) : Optional<string> {
		return Main.compileOperator(input, " >= ").or(/*(*/) -  > Main.compileOperator(input, /* "==")*/).or(() -  > Main.compileOperator(input, " + ")).or(() -  > Main.compileOperator(input, " < ")).or(() -  > Main.compileOperator(input, " <= ")).or(() -  > Main.compileOperator(input, " || ")).or(() -  > Main.compileOperator(input, " != ")).or(() -  > Main.compileOperator(input, " - ")).or(() -  > Main.compileOperator(input, " && ")).or(() -  > Main.compileOperator(input, " > "));}
	isChar(strip : CharSequence) : boolean {
		return !strip.isEmpty() && '\'' == strip.charAt(0) && '\'' == strip.charAt(strip.length() - 1) && 3 <= strip.length();}
	compileOperator(input : string, operator : string) : Optional<string> {
		let i : any = input.indexOf(operator);
		if (0 > i)
			return Optional.empty();
		let leftSlice : any = input.substring(0, i);
		let rightSlice : any = input.substring(i + operator.length());
		return Main.compileValue(leftSlice).flatMap(left => Main.compileValue(rightSlice).map(right => left + " " + operator + " " + right));}
	compileInvokable(input : string) : Optional<string> {
		let strip : any = input.strip();
		if (strip.isEmpty() || ')' != strip.charAt(strip.length() - 1))
			return Optional.empty();
		let withoutEnd : any = strip.substring(0, /* strip.length(*/) - ")".length(/*)*/);
		return Main.divide(withoutEnd, /* Main::foldInvocation*/).popLast().flatMap(/*Main::handleInvocationSegments*/);}
	foldInvocation(state : State, c : char) : State {
		let appended : any = state.append(c);
		if ('(' == c){
			let entered : any = appended.enter();
			if (entered.isShallow())
				return entered.advance();/*
            else
                return entered;*/
		}
		if (')' == c)
			return appended.exit();
		return appended;}
	isSymbol(input : CharSequence) : boolean {
		let length : any = input.length();
		let i : /*(var*/ = 0;/* i < length;*//* i++) {
            final var c = input.charAt(i);
            if (Character.isLetter(c) || (0 != i && Character.isDigit(c)))
                continue;
            return false;
        }*/
		return true;}
	isNumber(input : CharSequence) : boolean {
		let length : any = input.length();
		let i : /*(var*/ = 0;/* i < length;*//* i++) {
            final var c = input.charAt(i);
            if (!Character.isDigit(c))
                return false;
        }*/
		return true;}
	parseDefinitionOrPlaceholder(input : string) : Assignable {
		let strip : any = input.strip();
		return Main.parseDefinition(strip). < Assignable > map(value => value).orElseGet(() -  > new Placeholder(strip));}
	parseDefinition(strip : string) : Optional<Definition> {
		let separator : any = strip.lastIndexOf(' ');
		if (0 > separator)
			return Optional.empty();
		let beforeName : any = strip.substring(0, separator);
		let name : any = strip.substring(separator + " ".length());
		let divisions : any = Main.divide(beforeName, /* Main::foldTypeSeparator*/);/*
        return divisions.popLast().flatMap(tuple -> {
            final var beforeType = tuple.left().stream().collect(Collectors.joining(" "));
            final var type = tuple.right();
            return Optional.of(new Definition(Lists.empty(), beforeType, name, Main.compileType(type)));
        }*//*);*/}
	foldTypeSeparator(state : State, c : Character) : State {
		if (' ' == c && state.isLevel())
			return state.advance();
		let appended : any = state.append(c);
		if ('<' == c)
			return appended.enter();
		if ('>' == c)
			return appended.exit();
		return appended;}
	compileType(input : string) : string {
		let strip : any = input.strip();
		if ("var".contentEquals(strip))
			return "any";
		if ("String".contentEquals(strip))
			return "string";
		if ("int".contentEquals(strip))
			return "number";
		if (!strip.isEmpty() && '>' == strip.charAt(strip.length() - 1)){
			let withoutEnd : any = strip.substring(0, /* strip.length(*/) - " > ".length(/*)*/);
			let start : any = withoutEnd.indexOf('<');
			if (0 <= start){
				let base : any = withoutEnd.substring(0, start);
				let argument : any = withoutEnd.substring(start + " < ".length());
				let compiled : any = Main.compileValues(argument, /* Main::compileType*/);
				return base + " < " + compiled + " > ";
			}
		}
		if (strip.endsWith("[]")){
			let slice : any = strip.substring(0, /* strip.length(*/) - "[]".length(/*)*/);
			let compiled : any = Main.compileType(slice);
			return compiled + "[]";
		}
		if (Main.isSymbol(strip))
			return strip;
		return Placeholder.generate(strip);}
	compileValues(input : CharSequence, mapper : Function<string, string>) : string {
		return Main.compileAll(input, /* Main::foldValues*/, mapper, ", ");}
	foldValues(state : State, c : char) : State {
		if (',' == c && state.isLevel())
			return state.advance();
		let appended : any = state.append(c);
		if ('-' == c){
			let peek : any = appended.peek();
			if (peek.isPresent() && peek.get().equals('>'))
				return appended.popAndAppendToOption().orElse(appended);
		}
		if ('<' == c || '(' == c)
			return appended.enter();
		if ('>' == c || ')' == c)
			return appended.exit();
		return appended;}
	parseStructureHeader(input : string) : StructureDefinition {
		return Main.parseClassHeader(input, "class", "class").or(/*(*/) -  > Main.parseClassHeader(input, "record", /* "class")*/).or(() -  > Main.parseClassHeader(input, "interface", "interface")).orElseGet(() -  > new Placeholder(input));}
	parseClassHeader(input : string, keyword : string, type : string) : Optional<StructureDefinition> {
		let classIndex : any = input.indexOf(keyword + " ");
		if (0 > classIndex)
			return Optional.empty();
		let beforeKeyword : any = input.substring(0, classIndex).strip();
		let afterKeyword : any = input.substring(classIndex + (keyword + " ").length()).strip();
		let implementsIndex : any = afterKeyword.indexOf("implements ");
		if (0 <= implementsIndex){
			let beforeImplements : any = afterKeyword.substring(0, implementsIndex).strip();
			let afterImplements : any = afterKeyword.substring(implementsIndex + "implements ".length()).strip();
			return Optional.of(Main.complete(type, beforeKeyword, beforeImplements, Optional.of(afterImplements)));
		}/* else
            return Optional.of(Main.complete(type, beforeKeyword, afterKeyword, Optional.empty()));*/}
	complete(type : string, beforeKeyword : string, beforeImplements : string, maybeImplements : Optional<string>) : StructureHeader {
		let strip : any = beforeImplements.strip();
		if (!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1)){
			let withoutEnd : any = strip.substring(0, /* strip.length(*/) - ")".length(/*)*/);
			let contentStart : any = withoutEnd.indexOf('(');
			if (0 <= contentStart){
				let strip1 : any = withoutEnd.substring(0, contentStart).strip();
				return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, strip1);
			}
		}
		return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, beforeImplements);}
	parseStructureHeaderByAnnotations(type : string, beforeKeyword : string, maybeImplements : Optional<string>, strip1 : string) : StructureHeader {
		let index : any = beforeKeyword.lastIndexOf(System.lineSeparator());
		if (0 <= index){
			let annotations : any = Arrays.stream(Pattern.compile("\\n").split(beforeKeyword.substring(0, index).strip())).map(/*String::strip*/).filter(value => !value.isEmpty()).map(value => value.substring(1)).toList();
			let substring1 : any = beforeKeyword.substring(index + System.lineSeparator().length());
			return new StructureHeader(type, annotations, substring1, strip1, maybeImplements);
		}
		return new StructureHeader(type, Collections.emptyList(), beforeKeyword, strip1, maybeImplements);}
	divide(input : CharSequence, foldStatements : BiFunction<State, Character, State>) : ListLike<string> {
		let current : State = new MutableState(input);/*
        while (true) {
            final var maybe = current.pop();
            if (maybe.isEmpty())
                break;

            final var tuple = maybe.get();
            current = tuple.left();
            current = Main.fold(current, tuple.right(), foldStatements);
        }*/
		return current.advance().unwrap();}
	fold(state : State, c : char, folder : BiFunction<State, Character, State>) : State {
		return Main.foldSingleQuotes(state, c).or(/*(*/) -  > Main.foldDoubleQuotes(state, /* c)*/).orElseGet(() -  > folder.apply(state, c));}
	foldDoubleQuotes(state : State, c : char) : Optional<State> {
		if ('\"' != c)
			return Optional.empty();
		let current : any = state.append('\"');/*
        while (true) {
            final var maybeTuple = current.popAndAppendToTuple();
            if (maybeTuple.isEmpty())
                break;

            final var tuple = maybeTuple.get();
            current = tuple.left();

            final var next = tuple.right();
            if ('\\' == next)
                current = current.popAndAppendToOption().orElse(current);
            if ('\"' == next)
                break;
        }*/
		return Optional.of(current);}
	foldSingleQuotes(state : State, c : char) : Optional<State> {
		if ('\'' != c)
			return Optional.empty();
		return state.append(c).popAndAppendToTuple().flatMap(/*
                            tuple -> '\\' == tuple.right() ? tuple.left().popAndAppendToOption() : Optional.of(tuple.left())*/).flatMap(/*State::popAndAppendToOption*/);}
	foldStatements(state : State, c : char) : State {
		let appended : any = state.append(c);
		if (';' == c && appended.isLevel())
			return appended.advance();
		if ('}' == c && appended.isShallow())
			return appended.exit().advance();
		if ('{' == c)
			return appended.enter();
		if ('}' == c)
			return appended.exit();
		return appended;}
	handleInvocationSegments(tuple : Tuple<ListLike<string>, string>) : Optional<string> {
		let joined : any = tuple.left().stream().collect(Collectors.joining());
		if (joined.isEmpty() || '(' != joined.charAt(joined.length() - 1))
			return Optional.empty();
		let substring : any = joined.substring(0, /* joined.length(*/) - "(".length(/*)*/);
		let argument : any = tuple.right();
		return Main.compileCaller(substring).map(caller => caller + "(" + Main.compileValues(argument, /* Main::compileValueOrPlaceholder*/) + ")");}
	compileCaller(input : string) : Optional<string> {
		let strip : any = input.strip();
		if (strip.startsWith("new ")){
			let substring : any = strip.substring("new ".length());
			return Optional.of("new " + Main.compileType(substring));
		}
		return Main.compileValue(strip);}
}

