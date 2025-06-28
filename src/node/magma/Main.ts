












class Main {
	private static final let LINE_SEPARATOR : string = System.lineSeparator();
	constructor () {
	}
	main(final args : string[]) : void {
		final let root : any = Paths.get(".", "src", "java");
		JavaFiles.walk(root).match(files => {
			final let sources : any = files.stream().filter(path => path.toString().endsWith(".java")).toList();
			return Main.runWithSources(sources, root);
		}, arg => Some.new(arg)).ifPresent(arg => Throwable.printStackTrace(arg));
	}
	runWithSources(final sources : Iterable<Path>, final root : Path) : Optional<IOException> {/*
        for (final var source : sources) {
            final var maybe = Main.runWithSource(root, source);
            if (maybe.isPresent())
                return maybe;
        }*/
		return new None<>();
	}
	runWithSource(final root : Path, final source : Path) : Optional<IOException> {
		final let relative : any = root.relativize(source.getParent());
		return JavaFiles.readString(source).match(input => Main.runWithInput(source, input, relative), arg => Some.new(arg));
	}
	runWithInput(final source : Path, final input : CharSequence, final relative : Path) : Optional<IOException> {
		final let output : any = Main.compileRoot(input);
		final let targetParent : any = Paths.get(".", "src", "node").resolve(relative);
		return Main.extracted1(targetParent).or(/*(*/) -  > Main.compileAndWrite(source, targetParent, /* output)*/);
	}
	compileAndWrite(final source : Path, final targetParent : Path, final output : CharSequence) : Optional<IOException> {
		final let fileName : any = source.getFileName().toString();
		final let separator : any = fileName.lastIndexOf('.');
		final let name : any = fileName.substring(0, separator);
		final let target : any = targetParent.resolve(name + ".ts");
		return JavaFiles.writeString(target, output);
	}
	extracted1(final targetParent : Path) : Optional<IOException> {
		if (!Files.exists(targetParent))
			return JavaFiles.createDirectories(targetParent);
		return new None<>();
	}
	compileRoot(final input : CharSequence) : string {
		return Main.compileStatements(input, arg => Main.compileRootSegment(arg));
	}
	compileStatements(final input : CharSequence, final mapper : Function<string, string>) : string {
		return Main.compileAll(input, arg => Main.foldStatements(arg), mapper, "");
	}
	compileAll(final input : CharSequence, final folder : BiFunction<State, Character, State>, final mapper : Function<string, string>, final delimiter : CharSequence) : string {
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}
	compileRootSegment(final input : string) : string {
		return Main.compileRootSegmentValue(input.strip()) + Main.LINE_SEPARATOR;
	}
	compileRootSegmentValue(final input : string) : string {
		if (input.isBlank())
			return "";
		return Main.compileNamespaced(input).or(/*(*/) -  > Main.compileStructure(/*input)*/).orElseGet(() -  > Placeholder.generate(input));
	}
	compileNamespaced(final input : string) : Optional<string> {
		final let strip : any = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import "))
			return new Some<>("");
		return new None<>();
	}
	compileStructure(final input : string) : Optional<string> {
		if (input.isEmpty() || '}' != input.charAt(input.length() - 1))
			return new None<>();
		final let withoutEnd : any = input.substring(0, /* input.length(*/) - "}".length(/*)*/);
		final let contentStart : any = withoutEnd.indexOf('{');
		if (0 > contentStart)
			return new None<>();
		final let beforeContent : any = withoutEnd.substring(0, contentStart);
		final let content : any = withoutEnd.substring(contentStart + "{".length());
		final let definition : any = Main.parseStructureHeader(beforeContent);/*
        final String structName;*/
		if (/*definition instanceof final StructureHeader header*/){
			if (header.annotations().contains("Actual"))
				return new Some<>("");
			structName = header.name();
		}
		else 
			structName = "?";
		return new Some<>(/*definition.generate(*/) + " {" + Main.compileStatements(content, input1 => Main.compileStructureSegment(input1, /* structName)*/) + Main.LINE_SEPARATOR + "}");
	}
	compileStructureSegment(final input : string, final structName : CharSequence) : string {
		final let strip : any = input.strip();
		if (strip.isEmpty())
			return "";
		return Main.LINE_SEPARATOR + "\t" + Main.compileStructureSegmentValue(strip, structName);
	}
	compileStructureSegmentValue(final input : string, final structName : CharSequence) : string {
		return Main.compileStatement(input, input1 => Main.compileAssignment(input1, 1)).or(() -  > Main.compileMethod(input, structName)).orElseGet(() -  > Placeholder.generate(input));
	}
	compileStatement(final input : string, final mapper : Function<string, Optional<string>>) : Optional<string> {
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
			return new None<>();
		final let withoutEnd : any = input.substring(0, /* input.length(*/) - ";".length(/*)*/);
		return mapper.apply(withoutEnd).map(result => result + ";");
	}
	compileMethod(final input : string, final structName : CharSequence) : Optional<string> {
		final let paramEnd : any = input.indexOf(')');
		if (0 > paramEnd)
			return new None<>();
		final let withParams : any = input.substring(0, paramEnd);
		final let paramStart : any = withParams.indexOf('(');
		if (0 > paramStart)
			return new None<>();
		final let definition : any = withParams.substring(0, paramStart);
		final let params : any = withParams.substring(paramStart + "(".length());
		final let joinedParams : any = "(" + Main.compileValues(params, arg => Main.compileParameter(arg)) + ")";
		final let withBraces : any = input.substring(paramEnd + ")".length()).strip();
		final let oldHeader : any = Main.parseMethodHeader(definition, structName);
		final let newHeader : any = Main.modifyMethodHeader(oldHeader);/*

        final String outputContent;*/
		if (";".contentEquals(withBraces) || (/*newHeader instanceof final Definition definition1 && definition1.annotations().contains("Actual")*/))
			outputContent = ";";
		else 
			if (!withBraces.isEmpty() && '{' == withBraces.charAt(0) && '}' == withBraces.charAt(withBraces.length() - 1)){
				final let substring : any = withBraces.substring(1, withBraces.length() - 1);
				final let compiled : any = Main.compileFunctionSegments(substring, 2);
				outputContent = " {" + compiled + Main.LINE_SEPARATOR + "\t}";
			}
		else 
			return new None<>();
		return new Some<>(newHeader.generateWithAfterName(joinedParams) + outputContent);
	}
	modifyMethodHeader(final header : MethodHeader) : MethodHeader {/*
        return switch (header) {
            case final Definition definition -> definition.mapModifiers(oldModifiers -> {
                final var newModifiers = Lists.<String>empty();
                return newModifiers;
            });
            default -> header;
        }*//*;*/
	}
	compileFunctionSegments(final substring : CharSequence, final depth : number) : string {
		return Main.compileStatements(substring, input => Main.compileFunctionSegment(input, depth));
	}
	compileParameter(final input : string) : string {
		if (input.isBlank())
			return "";
		return Main.parseDefinitionOrPlaceholder(input).generate();
	}
	compileFunctionSegment(final input : string, final depth : number) : string {
		if (input.isBlank())
			return "";
		return Main.compileConditional(input, depth).or(/*(*/) -  > Main.compileElse(input, /* depth)*/).or(() -  > Main.compileStatement(input, input1 => Main.compileFunctionStatementValue(input1, depth))).map(/*value -> System.lineSeparator(*/) + "\t".repeat(/*depth) + value*/).orElseGet(() -  > Placeholder.generate(input));
	}
	compileElse(final input : string, final depth : number) : Optional<string> {
		final let strip : any = input.strip();
		if (strip.startsWith("else")){
			final let substring : any = strip.substring("else".length());
			return new Some<>("else " + Main.compileBlockOrStatement(depth, substring));
		}
		else 
			return new None<>();
	}
	compileFunctionStatementValue(final input : string, final depth : number) : Optional<string> {
		return Main.compileReturn(input, depth).or(/*(*/) -  > Main.compileInvokable(input, /* depth)*/).or(() -  > Main.compileAssignment(input, depth));
	}
	compileReturn(final input : string, final depth : number) : Optional<string> {
		final let strip : any = input.strip();
		if (strip.startsWith("return ")){
			final let slice : any = strip.substring("return ".length());
			return new Some<>("return " + Main.compileValueOrPlaceholder(slice, depth));
		}
		return new None<>();
	}
	compileConditional(final input : string, final depth : number) : Optional<string> {
		final let strip : any = input.strip();
		if (!strip.startsWith("if"))
			return new None<>();
		final let slice : any = strip.substring("if".length()).strip();
		if (slice.isEmpty() || '(' != slice.charAt(0))
			return new None<>();
		final let substring : any = slice.substring(1);
		return Main.divide(substring, arg => Main.foldConditional(arg)).popFirst().flatMap(tuple => Main.compileConditionalSegments(tuple, depth));
	}
	compileConditionalSegments(final tuple : Tuple<string, ListLike<string>>, final depth : number) : Optional<string> {
		final let substring1 : any = tuple.left();
		if (substring1.isEmpty() || ')' != substring1.charAt(substring1.length() - 1))
			return new None<>();
		final let condition : any = substring1.substring(0, substring1.length() - 1);
		final let joined : any = tuple.right().stream().collect(Collectors.joining());
		final let compiled : any = Main.compileBlockOrStatement(depth, joined);
		return new Some<>("if (" + Main.compileValueOrPlaceholder(condition, depth) + ")" + compiled);
	}
	compileBlockOrStatement(final depth : number, final input : string) : string {
		return Main.compileBlock(depth, input).orElseGet(/*(*/) -  > Main.compileFunctionSegment(input.strip(), /* depth + 1)*/);
	}
	compileBlock(final depth : number, final input : string) : Optional<string> {
		if (!Main.isBlock(input.strip()))
			return new None<>();
		final let compiled1 : any = Main.compileFunctionSegments(input.strip().substring(1, input.strip().length() - 1), depth + 1);
		final let compiled : string = "{" + compiled1 + Main.LINE_SEPARATOR + "\t".repeat(depth) + "}";
		return new Some<>(compiled);
	}
	isBlock(final withBraces : CharSequence) : boolean {
		return !withBraces.isEmpty() && '{' == withBraces.charAt(0) && '}' == withBraces.charAt(withBraces.length() - 1);
	}
	foldConditional(final state : State, final c : char) : State {
		final let appended : any = state.append(c);
		if ('(' == c)
			return appended.enter();
		if (')' == c){
			if (appended.isLevel())
				return appended.advance();
			return appended.exit();
		}
		return appended;
	}
	parseMethodHeader(final input : string, final structName : CharSequence) : MethodHeader {
		return Main.parseConstructor(input, structName).orElseGet(/*(*/) -  > Main.parseDefinitionOrPlaceholder(/*input)*/);
	}
	parseConstructor(final input : string, final structName : CharSequence) : Optional<MethodHeader> {
		final let strip : any = input.strip();
		final let index : any = strip.lastIndexOf(' ');
		if (0 <= index){
			final let name : any = strip.substring(index + " ".length()).strip();
			if (name.contentEquals(structName))
				return new Some<>(new Constructor());
		}
		return new None<>();
	}
	compileAssignment(final input : string, final depth : number) : Optional<string> {
		final let separator : any = input.indexOf('=');
		if (0 <= separator){
			final let before : any = input.substring(0, separator);
			final let after : any = input.substring(separator + "=".length());
			final let assignable : any = Main.parseDefinitionOrPlaceholder(before);/*
            final Assignable assignable1;*/
			if (/*assignable instanceof final Definition definition*/)
				assignable1 = definition.withModifier("let");
			else 
				assignable1 = assignable;
			return new Some<>(/*assignable1.generate(*/) + " = " + Main.compileValueOrPlaceholder(after, /* depth)*/);
		}
		return new None<>();
	}
	compileValueOrPlaceholder(final input : string, final depth : number) : string {
		return Main.compileValue(input, depth).orElseGet(/*(*/) -  > Placeholder.generate(/*input)*/);
	}
	compileValue(final input : string, final depth : number) : Optional<string> {
		final let maybeLambda : any = Main.compileLambda(input, depth);
		if (maybeLambda.isPresent())
			return maybeLambda;
		final let maybeOperator : any = Main.compileOperators(input, depth);
		if (maybeOperator.isPresent())
			return maybeOperator;
		final let maybeInvocation : any = Main.compileInvokable(input, depth);
		if (maybeInvocation.isPresent())
			return maybeInvocation;
		final let dataSeparator : any = input.lastIndexOf('.');
		if (0 <= dataSeparator){
			final let value : any = input.substring(0, dataSeparator);
			final let property : any = input.substring(dataSeparator + ".".length()).strip();
			if (Main.isSymbol(property))
				return Main.compileValue(value, depth).map(result => result + "." + property);
		}
		final let methodSeparator : any = input.lastIndexOf("::");
		if (0 <= methodSeparator){
			final let value : any = input.substring(0, methodSeparator);
			final let property : any = input.substring(methodSeparator + "::".length()).strip();
			if (Main.isSymbol(property))
				return Main.compileValue(value, depth).map(result => "arg => " + result + "." + property + "(arg)");
		}
		final let strip : any = input.strip();
		if (!strip.isEmpty() && '!' == strip.charAt(0)){
			final let substring : any = strip.substring(1);
			return Main.compileValue(substring, depth).map(value => "!" + value);
		}
		if (Main.isNumber(strip))
			return new Some<>(strip);
		if (!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == strip.charAt(strip.length() - 1))
			return new Some<>(strip);
		if (Main.isChar(strip))
			return new Some<>(strip);
		if (Main.isSymbol(strip))
			return new Some<>(strip);
		return new None<>();
	}
	compileLambda(final input : string, final depth : number) : Optional<string> {
		final let arrowIndex : any = input.indexOf(" -  > ");
		if (0 > arrowIndex)
			return new None<>();
		final let before : any = input.substring(0, arrowIndex).strip();
		if (!Main.isSymbol(before))
			return new None<>();
		final let after : any = input.substring(arrowIndex + " -  > ".length());
		return Main.compileBlock(depth, after).or(/*(*/) -  > Main.compileValue(after, /* depth))
                   .map(afterResult -> before + " => " + afterResult*/);
	}
	compileOperators(final input : string, final depth : number) : Optional<string> {
		return Main.compileOperator(input, " >= ", depth).or(/*(*/) -  > Main.compileOperator(input, " == ", /* depth)*/).or(() -  > Main.compileOperator(input, " + ", depth)).or(() -  > Main.compileOperator(input, " < ", depth)).or(() -  > Main.compileOperator(input, " <= ", depth)).or(() -  > Main.compileOperator(input, " || ", depth)).or(() -  > Main.compileOperator(input, " != ", depth)).or(() -  > Main.compileOperator(input, " - ", depth)).or(() -  > Main.compileOperator(input, " && ", depth)).or(() -  > Main.compileOperator(input, " > ", depth));
	}
	isChar(final strip : CharSequence) : boolean {
		return !strip.isEmpty() && '\'' == strip.charAt(0) && '\'' == strip.charAt(strip.length() - 1) && 3 <= strip.length();
	}
	compileOperator(final input : string, final operator : string, final depth : number) : Optional<string> {
		final let i : any = input.indexOf(operator);
		if (0 > i)
			return new None<>();
		final let leftSlice : any = input.substring(0, i);
		final let rightSlice : any = input.substring(i + operator.length());
		return Main.compileValue(leftSlice, depth).flatMap(left => Main.compileValue(rightSlice, depth).map(right => left + " " + operator + " " + right));
	}
	compileInvokable(final input : string, final depth : number) : Optional<string> {
		final let strip : any = input.strip();
		if (strip.isEmpty() || ')' != strip.charAt(strip.length() - 1))
			return new None<>();
		final let withoutEnd : any = strip.substring(0, /* strip.length(*/) - ")".length(/*)*/);
		return Main.divide(withoutEnd, arg => Main.foldInvocation(arg)).popLast().flatMap(tuple => Main.handleInvocationSegments(tuple, depth));
	}
	foldInvocation(final state : State, final c : char) : State {
		final let appended : any = state.append(c);
		if ('(' == c){
			final let entered : any = appended.enter();
			if (entered.isShallow())
				return entered.advance();
			else 
				return entered;
		}
		if (')' == c)
			return appended.exit();
		return appended;
	}
	isSymbol(final input : CharSequence) : boolean {
		final let length : any = input.length();/*
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (Main.isSymbolChar(c, i))
                continue;
            return false;
        }*/
		return true;
	}
	isSymbolChar(final c : char, final i : number) : boolean {
		return Character.isLetter(c) || (0 != i && Character.isDigit(c)) || '_' == c;
	}
	isNumber(final input : CharSequence) : boolean {
		final let length : any = input.length();/*
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (!Character.isDigit(c))
                return false;
        }*/
		return true;
	}
	parseDefinitionOrPlaceholder(final input : string) : Assignable {
		final let strip : any = input.strip();
		return Main.parseDefinition(strip). < Assignable > map(value => value).orElseGet(() -  > new Placeholder(strip));
	}
	parseDefinition(final strip : string) : Optional<Definition> {
		final let separator : any = strip.lastIndexOf(' ');
		if (0 > separator)
			return new None<>();
		final let beforeName : any = strip.substring(0, separator);
		final let name : any = strip.substring(separator + " ".length());
		return Main.divide(beforeName, arg => Main.foldTypeSeparator(arg)).popLast().flatMap(tuple => {
			final let beforeType : any = tuple.left().stream().collect(Collectors.joining(" "));
			final let type : any = tuple.right();
			return Main.divide(beforeType, arg => Main.foldTypeSeparator(arg)).popLast().flatMap(typeParamDivisionsTuple => {
				final let joined : any = typeParamDivisionsTuple.left().stream().collect(Collectors.joining(" "));
				final let typeParamsString : any = typeParamDivisionsTuple.right().strip();
				if (typeParamsString.startsWith(" < ") && typeParamsString.endsWith(" > ")){
					final let slice : any = typeParamsString.substring(1, typeParamsString.length() - 1);
					final let typeParams : any = Main.divide(slice, arg => Main.foldValues(arg)).stream().map(arg => String.strip(arg)).filter(value => !value.isEmpty()).toList();
					return Main.assemble(joined, typeParams, name, type);
				}
				else 
					return new None<>();
			}).or(/*() -> {
                return Main.assemble(beforeType, Collections.emptyList(), name, type);
            }*/);
		}).or(/*() -> {
            return Main.assemble("", Collections.emptyList(), name, beforeName);
        }*/);
	}
	assemble(final beforeTypeParams : string, final typeParams : List<string>, final name : string, final type : string) : Optional<Definition> {
		final let annotationIndex : any = beforeTypeParams.lastIndexOf('\n');
		if (0 <= annotationIndex){
			final let annotationString : any = beforeTypeParams.substring(0, annotationIndex);
			final let modifiersString : any = beforeTypeParams.substring(annotationIndex + "\n".length());
			return new Some<>(new Definition(Main.parseAnnotations(annotationString), Main.parseModifiers(modifiersString), typeParams, name, Main.compileType(type)));
		}
		else {
			final let modifiers : any = Main.parseModifiers(beforeTypeParams);
			return new Some<>(new Definition(Lists.empty(), modifiers, typeParams, name, Main.compileType(type)));
		}
	}
	parseModifiers(final joined : string) : JavaList<string> {
		final let list : any = Main.divide(joined, Main.foldByDelimiter(' ')).stream().map(arg => String.strip(arg)).filter(value => !value.isEmpty()).collect(Collectors.toCollection(arg => ArrayList.new(arg)));
		return new JavaList<>(list);
	}
	foldByDelimiter(final delimiter : char) : BiFunction<State, Character, State> {/*
        return (state, c) -> {
            if (c == delimiter)
                return state.advance();
            return state.append(c);
        }*//*;*/
	}
	foldTypeSeparator(final state : State, final c : Character) : State {
		if (' ' == c && state.isLevel())
			return state.advance();
		final let appended : any = state.append(c);
		if ('<' == c)
			return appended.enter();
		if ('>' == c)
			return appended.exit();
		return appended;
	}
	compileType(final input : string) : string {
		final let strip : any = input.strip();
		if ("var".contentEquals(strip))
			return "any";
		if ("String".contentEquals(strip))
			return "string";
		if ("int".contentEquals(strip))
			return "number";
		if (!strip.isEmpty() && '>' == strip.charAt(strip.length() - 1)){
			final let withoutEnd : any = strip.substring(0, /* strip.length(*/) - " > ".length(/*)*/);
			final let start : any = withoutEnd.indexOf('<');
			if (0 <= start){
				final let base : any = withoutEnd.substring(0, start);
				final let argument : any = withoutEnd.substring(start + " < ".length());
				final let compiled : any = Main.compileValues(argument, arg => Main.compileType(arg));
				return base + " < " + compiled + " > ";
			}
		}
		if (strip.endsWith("[]")){
			final let slice : any = strip.substring(0, /* strip.length(*/) - "[]".length(/*)*/);
			final let compiled : any = Main.compileType(slice);
			return compiled + "[]";
		}
		if (Main.isSymbol(strip))
			return strip;
		return Placeholder.generate(strip);
	}
	compileValues(final input : CharSequence, final mapper : Function<string, string>) : string {
		return Main.compileAll(input, arg => Main.foldValues(arg), mapper, ", ");
	}
	foldValues(final state : State, final c : char) : State {
		if (',' == c && state.isLevel())
			return state.advance();
		final let appended : any = state.append(c);
		if ('-' == c){
			final let peek : any = appended.peek();
			if (peek.filter(value => '>' == value).isPresent())
				return appended.popAndAppendToOption().orElse(appended);
		}
		if ('<' == c || '(' == c)
			return appended.enter();
		if ('>' == c || ')' == c)
			return appended.exit();
		return appended;
	}
	parseStructureHeader(final input : string) : StructureDefinition {
		return Main.parseClassHeader(input, "class", "class").or(/*(*/) -  > Main.parseClassHeader(input, "record", /* "class")*/).or(() -  > Main.parseClassHeader(input, "interface", "interface")).orElseGet(() -  > new Placeholder(input));
	}
	parseClassHeader(final input : string, final keyword : string, final type : string) : Optional<StructureDefinition> {
		final let classIndex : any = input.indexOf(keyword + " ");
		if (0 > classIndex)
			return new None<>();
		final let beforeKeyword : any = input.substring(0, classIndex).strip();
		final let afterKeyword : any = input.substring(classIndex + (keyword + " ").length()).strip();
		final let implementsIndex : any = afterKeyword.indexOf("implements ");
		if (0 <= implementsIndex){
			final let beforeImplements : any = afterKeyword.substring(0, implementsIndex).strip();
			final let afterImplements : any = afterKeyword.substring(implementsIndex + "implements ".length()).strip();
			return new Some<>(Main.complete(type, beforeKeyword, beforeImplements, new Some<string>(afterImplements)));
		}
		else 
			return new Some<>(Main.complete(type, beforeKeyword, afterKeyword, new None<string>()));
	}
	complete(final type : string, final beforeKeyword : string, final beforeImplements : string, final maybeImplements : Optional<string>) : StructureHeader {
		final let strip : any = beforeImplements.strip();
		if (!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1)){
			final let withoutEnd : any = strip.substring(0, /* strip.length(*/) - ")".length(/*)*/);
			final let contentStart : any = withoutEnd.indexOf('(');
			if (0 <= contentStart){
				final let strip1 : any = withoutEnd.substring(0, contentStart).strip();
				return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, strip1);
			}
		}
		return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, beforeImplements);
	}
	parseStructureHeaderByAnnotations(final type : string, final beforeKeyword : string, final maybeImplements : Optional<string>, final strip1 : string) : StructureHeader {
		final let index : any = beforeKeyword.lastIndexOf(System.lineSeparator());
		if (0 <= index){
			final let annotations : any = Main.parseAnnotations(beforeKeyword.substring(0, index));
			final let substring1 : any = beforeKeyword.substring(index + System.lineSeparator().length());
			return new StructureHeader(type, annotations, substring1, strip1, maybeImplements);
		}
		return new StructureHeader(type, Lists.empty(), beforeKeyword, strip1, maybeImplements);
	}
	parseAnnotations(final input : string) : ListLike<string> {
		final let copy : any = Arrays.stream(Pattern.compile("\\n").split(input.strip())).map(arg => String.strip(arg)).filter(value => !value.isEmpty()).map(value => value.substring(1)).collect(Collectors.toCollection(arg => ArrayList.new(arg)));
		return new JavaList<>(copy);
	}
	divide(final input : CharSequence, final folder : BiFunction<State, Character, State>) : ListLike<string> {
		let current : State = new MutableState(input);/*
        while (true) {
            final var maybe = current.pop().toTuple(new Tuple<>(current, '\0'));
            if (maybe.left()) {
                final var tuple = maybe.right();
                current = tuple.left();
                current = Main.fold(current, tuple.right(), folder);
            } else
                break;
        }*/
		return current.advance().unwrap();
	}
	fold(final state : State, final c : char, final folder : BiFunction<State, Character, State>) : State {
		return Main.foldSingleQuotes(state, c).or(/*(*/) -  > Main.foldDoubleQuotes(state, /* c)*/).orElseGet(() -  > folder.apply(state, c));
	}
	foldDoubleQuotes(final state : State, final c : char) : Optional<State> {
		if ('\"' != c)
			return new None<>();
		final let current : any = state.append('\"');/*
        while (true) {
            final var maybeTuple =
                    current.popAndAppendToTuple().flatMap(tuple -> Main.getObjectOptional(tuple, current));
            if (maybeTuple.isEmpty())
                break;
        }*/
		return new Some<>(current);
	}
	getObjectOptional(final tuple : Tuple<State, Character>, final current : State) : Optional<State> {
		final let left : any = tuple.left();
		final let next : any = tuple.right();
		if ('\\' == next)
			return new Some<>(left.popAndAppendToOption().orElse(current));
		if ('\"' == next)
			return new None<>();
		return new Some<>(left);
	}
	foldSingleQuotes(final state : State, final c : char) : Optional<State> {
		if ('\'' != c)
			return new None<>();
		return state.append(c).popAndAppendToTuple().flatMap(tuple => {
			if ('\\' == tuple.right())
				return tuple.left().popAndAppendToOption();
			return new Some<>(tuple.left());
		}).flatMap(arg => State.popAndAppendToOption(arg));
	}
	foldStatements(final state : State, final c : char) : State {
		final let appended : any = state.append(c);
		if (';' == c && appended.isLevel())
			return appended.advance();
		if ('}' == c && appended.isShallow())
			return appended.exit().advance();
		if ('{' == c || '(' == c)
			return appended.enter();
		if ('}' == c || ')' == c)
			return appended.exit();
		return appended;
	}
	handleInvocationSegments(final tuple : Tuple<ListLike<string>, string>, final depth : number) : Optional<string> {
		final let joined : any = tuple.left().stream().collect(Collectors.joining());
		if (joined.isEmpty() || '(' != joined.charAt(joined.length() - 1))
			return new None<>();
		final let substring : any = joined.substring(0, /* joined.length(*/) - "(".length(/*)*/);
		final let argument : any = tuple.right();
		return Main.compileCaller(substring, depth).map(caller => caller + "(" + Main.compileValues(argument, input => Main.compileValueOrPlaceholder(input, depth)) + ")");
	}
	compileCaller(final input : string, final depth : number) : Optional<string> {
		final let strip : any = input.strip();
		if (strip.startsWith("new ")){
			final let substring : any = strip.substring("new ".length());
			return new Some<>("new " + Main.compileType(substring));
		}
		return Main.compileValue(strip, depth);
	}
}

