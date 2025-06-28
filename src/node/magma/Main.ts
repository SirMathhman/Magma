












class Main {
	constructor () {
	}
	LINE_SEPARATOR : string = System.lineSeparator();
	constructor () {
	}
	main(args : string[]) : void {
		let root : any = Paths.get(".", "src", "java");
		JavaFiles.walk(root).match(files => {
			let sources : any = files.stream().filter(path => path.toString().endsWith(".java")).toList();
			return Main.runWithSources(sources, root);
		}, arg => Some.new(arg)).ifPresent(arg => Throwable.printStackTrace(arg));
	}
	runWithSources(sources : Iterable<Path>, root : Path) : Optional<IOException> {/*
        for (final var source : sources) {
            final var maybe = Main.runWithSource(root, source);
            if (maybe.isPresent())
                return maybe;
        }*/
		return new None();
	}
	runWithSource(root : Path, source : Path) : Optional<IOException> {
		let relative : any = root.relativize(source.getParent());
		return JavaFiles.readString(source).match(input => Main.runWithInput(source, input, relative), arg => Some.new(arg));
	}
	runWithInput(source : Path, input : CharSequence, relative : Path) : Optional<IOException> {
		let output : any = Main.compileRoot(input);
		let targetParent : any = Paths.get(".", "src", "node").resolve(relative);
		return Main.extracted1(targetParent).or(/*(*/) -  > Main.compileAndWrite(source, targetParent, /* output)*/);
	}
	compileAndWrite(source : Path, targetParent : Path, output : CharSequence) : Optional<IOException> {
		let fileName : any = source.getFileName().toString();
		let separator : any = fileName.lastIndexOf('.');
		let name : any = fileName.substring(0, separator);
		let target : any = targetParent.resolve(name + ".ts");
		return JavaFiles.writeString(target, output);
	}
	extracted1(targetParent : Path) : Optional<IOException> {
		if (!Files.exists(targetParent))
			return JavaFiles.createDirectories(targetParent);
		return new None();
	}
	compileRoot(input : CharSequence) : string {
		return Main.compileStatements(input, arg => Main.compileRootSegment(arg));
	}
	compileStatements(input : CharSequence, mapper : Function<string, string>) : string {
		return Main.compileAll(input, arg => Main.foldStatements(arg), mapper, "");
	}
	compileAll(input : CharSequence, folder : BiFunction<State, Character, State>, mapper : Function<string, string>, delimiter : CharSequence) : string {
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}
	compileRootSegment(input : string) : string {
		return Main.compileRootSegmentValue(input.strip()) + Main.LINE_SEPARATOR;
	}
	compileRootSegmentValue(input : string) : string {
		if (input.isBlank())
			return "";
		return Main.compileNamespaced(input).or(/*(*/) -  > Main.compileStructure(/*input)*/).orElseGet(() -  > Placeholder.generate(input));
	}
	compileNamespaced(input : string) : Optional<string> {
		let strip : any = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import "))
			return new Some("");
		return new None();
	}
	compileStructure(input : string) : Optional<string> {
		if (input.isEmpty() || '}' != input.charAt(input.length() - 1))
			return new None();
		let withoutEnd : any = input.substring(0, /* input.length(*/) - "}".length(/*)*/);
		let contentStart : any = withoutEnd.indexOf('{');
		if (0 > contentStart)
			return new None();
		let beforeContent : any = withoutEnd.substring(0, contentStart);
		let content : any = withoutEnd.substring(contentStart + "{".length());
		let definition : any = Main.parseStructureHeader(beforeContent);/*
        final String structName;*//*
        final String joined;*/
		if (/*definition instanceof final StructureHeader header*/){
			if (header.annotations().contains("Actual"))
				return new Some("");
			structName = header.name();
			let joinedParameters : any = header.parameters().stream().map(arg => Parameter.generate(arg)).map(result => Main.LINE_SEPARATOR + "\t" + result + ";").collect(Collectors.joining());
			let constructorParams : any = header.parameters().stream().map(arg => Parameter.generate(arg)).collect(Collectors.joining(", "));
			joined = joinedParameters + "\n\tconstructor (" + constructorParams + ") {\n\t}";
		}
		else {
			structName = "?";
			joined = "";
		}
		let compiled : any = Main.compileStatements(content, input1 => Main.compileStructureSegment(input1, structName));
		return new Some(definition.generate() + " {" + joined + compiled + Main.LINE_SEPARATOR + "}");
	}
	compileStructureSegment(input : string, structName : CharSequence) : string {
		let strip : any = input.strip();
		if (strip.isEmpty())
			return "";
		return Main.LINE_SEPARATOR + "\t" + Main.compileStructureSegmentValue(strip, structName);
	}
	compileStructureSegmentValue(input : string, structName : CharSequence) : string {
		return Main.compileStatement(input, input1 => Main.parseAssignment(input1, 1).map(arg => Assignment.generate(arg))).or(() -  > Main.compileMethod(input, structName)).orElseGet(() -  > Placeholder.generate(input));
	}
	compileStatement(input : string, mapper : Function<string, Optional<string>>) : Optional<string> {
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
			return new None();
		let withoutEnd : any = input.substring(0, /* input.length(*/) - ";".length(/*)*/);
		return mapper.apply(withoutEnd).map(result => result + ";");
	}
	compileMethod(input : string, structName : CharSequence) : Optional<string> {
		let paramEnd : any = input.indexOf(')');
		if (0 > paramEnd)
			return new None();
		let withParams : any = input.substring(0, paramEnd);
		let paramStart : any = withParams.indexOf('(');
		if (0 > paramStart)
			return new None();
		let definition : any = withParams.substring(0, paramStart);
		let params : any = withParams.substring(paramStart + "(".length());
		let joinedParams : any = "(" + Main.compileParameters(params) + ")";
		let withBraces : any = input.substring(paramEnd + ")".length()).strip();
		let oldHeader : any = Main.parseMethodHeader(definition, structName);
		let newHeader : any = Main.modifyMethodHeader(oldHeader);/*

        final String outputContent;*/
		if (";".contentEquals(withBraces) || (/*newHeader instanceof final Definition definition1 && definition1.annotations().contains("Actual")*/))
			outputContent = ";";
		else 
			if (!withBraces.isEmpty() && '{' == withBraces.charAt(0) && '}' == withBraces.charAt(withBraces.length() - 1)){
				let substring : any = withBraces.substring(1, withBraces.length() - 1);
				let compiled : any = Main.compileFunctionSegments(substring, 2);
				outputContent = " {" + compiled + Main.LINE_SEPARATOR + "\t}";
			}
		else 
			return new None();
		return new Some(newHeader.generateWithAfterName(joinedParams) + outputContent);
	}
	compileParameters(params : CharSequence) : string {
		return Main.compileValues(params, input => Main.parseParameter(input).generate());
	}
	modifyMethodHeader(header : MethodHeader) : MethodHeader {/*
        return switch (header) {
            case final Definition definition -> definition.mapModifiers(oldModifiers -> {
                return Lists.empty();
            });
            default -> header;
        }*//*;*/
	}
	compileFunctionSegments(substring : CharSequence, depth : number) : string {
		return Main.compileStatements(substring, input => Main.compileFunctionSegment(input, depth));
	}
	parseParameter(input : string) : Parameter {
		if (input.isBlank())
			return new Whitespace();
		return Main.parseDefinitionOrPlaceholder(input);
	}
	compileFunctionSegment(input : string, depth : number) : string {
		if (input.isBlank())
			return "";
		return Main.compileConditional(input, depth).or(/*(*/) -  > Main.compileElse(input, /* depth)*/).or(() -  > Main.compileStatement(input, input1 => Main.compileFunctionStatementValue(input1, depth))).map(/*value -> System.lineSeparator(*/) + "\t".repeat(/*depth) + value*/).orElseGet(() -  > Placeholder.generate(input));
	}
	compileElse(input : string, depth : number) : Optional<string> {
		let strip : any = input.strip();
		if (strip.startsWith("else")){
			let substring : any = strip.substring("else".length());
			return new Some("else " + Main.compileBlockOrStatement(depth, substring));
		}
		else 
			return new None();
	}
	compileFunctionStatementValue(input : string, depth : number) : Optional<string> {
		return Main.compileReturn(input, depth).or(/*(*/) -  > Main.compileInvokable(input, /* depth)*/).or(() -  > Main.parseAssignment(input, depth).map(assignment => {
			return assignment.mapAssignable(assignable => {
				if (/*assignable instanceof final Definition definition*/)
					return definition.withModifier("let");
				return assignable;
			});
		}).map(arg => Assignment.generate(arg)));
	}
	compileReturn(input : string, depth : number) : Optional<string> {
		let strip : any = input.strip();
		if (strip.startsWith("return ")){
			let slice : any = strip.substring("return ".length());
			return new Some("return " + Main.compileValueOrPlaceholder(slice, depth));
		}
		return new None();
	}
	compileConditional(input : string, depth : number) : Optional<string> {
		let strip : any = input.strip();
		if (!strip.startsWith("if"))
			return new None();
		let slice : any = strip.substring("if".length()).strip();
		if (slice.isEmpty() || '(' != slice.charAt(0))
			return new None();
		let substring : any = slice.substring(1);
		return Main.divide(substring, arg => Main.foldConditional(arg)).popFirst().flatMap(tuple => Main.compileConditionalSegments(tuple, depth));
	}
	compileConditionalSegments(tuple : Tuple<string, ListLike<string>>, depth : number) : Optional<string> {
		let substring1 : any = tuple.left();
		if (substring1.isEmpty() || ')' != substring1.charAt(substring1.length() - 1))
			return new None();
		let condition : any = substring1.substring(0, substring1.length() - 1);
		let joined : any = tuple.right().stream().collect(Collectors.joining());
		let compiled : any = Main.compileBlockOrStatement(depth, joined);
		return new Some("if (" + Main.compileValueOrPlaceholder(condition, depth) + ")" + compiled);
	}
	compileBlockOrStatement(depth : number, input : string) : string {
		return Main.compileBlock(depth, input).orElseGet(/*(*/) -  > Main.compileFunctionSegment(input.strip(), /* depth + 1)*/);
	}
	compileBlock(depth : number, input : string) : Optional<string> {
		if (!Main.isBlock(input.strip()))
			return new None();
		let compiled1 : any = Main.compileFunctionSegments(input.strip().substring(1, input.strip().length() - 1), depth + 1);
		let compiled : string = "{" + compiled1 + Main.LINE_SEPARATOR + "\t".repeat(depth) + "}";
		return new Some(compiled);
	}
	isBlock(withBraces : CharSequence) : boolean {
		return !withBraces.isEmpty() && '{' == withBraces.charAt(0) && '}' == withBraces.charAt(withBraces.length() - 1);
	}
	foldConditional(state : State, c : char) : State {
		let appended : any = state.append(c);
		if ('(' == c)
			return appended.enter();
		if (')' == c){
			if (appended.isLevel())
				return appended.advance();
			return appended.exit();
		}
		return appended;
	}
	parseMethodHeader(input : string, structName : CharSequence) : MethodHeader {
		return Main.parseConstructor(input, structName).orElseGet(/*(*/) -  > Main.parseDefinitionOrPlaceholder(/*input)*/);
	}
	parseConstructor(input : string, structName : CharSequence) : Optional<MethodHeader> {
		let strip : any = input.strip();
		let index : any = strip.lastIndexOf(' ');
		if (0 <= index){
			let name : any = strip.substring(index + " ".length()).strip();
			if (name.contentEquals(structName))
				return new Some(new Constructor());
		}
		return new None();
	}
	parseAssignment(input : string, depth : number) : Optional<Assignment> {
		let separator : any = input.indexOf('=');
		if (0 <= separator){
			let before : any = input.substring(0, separator);
			let after : any = input.substring(separator + "=".length());
			let assignable : any = Main.parseDefinitionOrPlaceholder(before);
			return new Some(new Assignment(assignable, Main.compileValueOrPlaceholder(after, depth)));
		}
		return new None();
	}
	compileValueOrPlaceholder(input : string, depth : number) : string {
		return Main.compileValue(input, depth).orElseGet(/*(*/) -  > Placeholder.generate(/*input)*/);
	}
	compileValue(input : string, depth : number) : Optional<string> {
		let maybeLambda : any = Main.compileLambda(input, depth);
		if (maybeLambda.isPresent())
			return maybeLambda;
		let maybeOperator : any = Main.compileOperators(input, depth);
		if (maybeOperator.isPresent())
			return maybeOperator;
		let maybeInvocation : any = Main.compileInvokable(input, depth);
		if (maybeInvocation.isPresent())
			return maybeInvocation;
		let dataSeparator : any = input.lastIndexOf('.');
		if (0 <= dataSeparator){
			let value : any = input.substring(0, dataSeparator);
			let property : any = input.substring(dataSeparator + ".".length()).strip();
			if (Main.isSymbol(property))
				return Main.compileValue(value, depth).map(result => result + "." + property);
		}
		let methodSeparator : any = input.lastIndexOf("::");
		if (0 <= methodSeparator){
			let value : any = input.substring(0, methodSeparator);
			let property : any = input.substring(methodSeparator + "::".length()).strip();
			if (Main.isSymbol(property))
				return Main.compileValue(value, depth).map(result => "arg => " + result + "." + property + "(arg)");
		}
		let strip : any = input.strip();
		if (!strip.isEmpty() && '!' == strip.charAt(0)){
			let substring : any = strip.substring(1);
			return Main.compileValue(substring, depth).map(value => "!" + value);
		}
		if (Main.isNumber(strip))
			return new Some(strip);
		if (!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == strip.charAt(strip.length() - 1))
			return new Some(strip);
		if (Main.isChar(strip))
			return new Some(strip);
		if (Main.isSymbol(strip))
			return new Some(strip);
		return new None();
	}
	compileLambda(input : string, depth : number) : Optional<string> {
		let arrowIndex : any = input.indexOf(" -  > ");
		if (0 > arrowIndex)
			return new None();
		let before : any = input.substring(0, arrowIndex).strip();
		if (!Main.isSymbol(before))
			return new None();
		let after : any = input.substring(arrowIndex + " -  > ".length());
		return Main.compileBlock(depth, after).or(/*(*/) -  > Main.compileValue(after, /* depth))
                   .map(afterResult -> before + " => " + afterResult*/);
	}
	compileOperators(input : string, depth : number) : Optional<string> {
		return Main.compileOperator(input, " >= ", depth).or(/*(*/) -  > Main.compileOperator(input, " == ", /* depth)*/).or(() -  > Main.compileOperator(input, " + ", depth)).or(() -  > Main.compileOperator(input, " < ", depth)).or(() -  > Main.compileOperator(input, " <= ", depth)).or(() -  > Main.compileOperator(input, " || ", depth)).or(() -  > Main.compileOperator(input, " != ", depth)).or(() -  > Main.compileOperator(input, " - ", depth)).or(() -  > Main.compileOperator(input, " && ", depth)).or(() -  > Main.compileOperator(input, " > ", depth));
	}
	isChar(strip : CharSequence) : boolean {
		return !strip.isEmpty() && '\'' == strip.charAt(0) && '\'' == strip.charAt(strip.length() - 1) && 3 <= strip.length();
	}
	compileOperator(input : string, operator : string, depth : number) : Optional<string> {
		let i : any = input.indexOf(operator);
		if (0 > i)
			return new None();
		let leftSlice : any = input.substring(0, i);
		let rightSlice : any = input.substring(i + operator.length());
		return Main.compileValue(leftSlice, depth).flatMap(left => Main.compileValue(rightSlice, depth).map(right => left + " " + operator + " " + right));
	}
	compileInvokable(input : string, depth : number) : Optional<string> {
		let strip : any = input.strip();
		if (strip.isEmpty() || ')' != strip.charAt(strip.length() - 1))
			return new None();
		let withoutEnd : any = strip.substring(0, /* strip.length(*/) - ")".length(/*)*/);
		return Main.divide(withoutEnd, arg => Main.foldInvocation(arg)).popLast().flatMap(tuple => Main.handleInvocationSegments(tuple, depth));
	}
	foldInvocation(state : State, c : char) : State {
		let appended : any = state.append(c);
		if ('(' == c){
			let entered : any = appended.enter();
			if (entered.isShallow())
				return entered.advance();
			else 
				return entered;
		}
		if (')' == c)
			return appended.exit();
		return appended;
	}
	isSymbol(input : CharSequence) : boolean {
		let length : any = input.length();/*
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (Main.isSymbolChar(c, i))
                continue;
            return false;
        }*/
		return true;
	}
	isSymbolChar(c : char, i : number) : boolean {
		return Character.isLetter(c) || (0 != i && Character.isDigit(c)) || '_' == c;
	}
	isNumber(input : CharSequence) : boolean {
		let length : any = input.length();/*
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (!Character.isDigit(c))
                return false;
        }*/
		return true;
	}
	parseDefinitionOrPlaceholder(input : string) : Assignable {
		let strip : any = input.strip();
		return Main.parseDefinition(strip). < Assignable > map(value => {
			let newModifiers : any = Lists. < String > empty();
			return new Definition(value.annotations(), newModifiers, value.typeParams(), value.name(), value.type());
		}).orElseGet(() -  > new Placeholder(strip));
	}
	parseDefinition(strip : string) : Optional<Definition> {
		let separator : any = strip.lastIndexOf(' ');
		if (0 > separator)
			return new None();
		let beforeName : any = strip.substring(0, separator);
		let name : any = strip.substring(separator + " ".length());
		return Main.divide(beforeName, arg => Main.foldTypeSeparator(arg)).popLast().flatMap(tuple => {
			let beforeType : any = tuple.left().stream().collect(Collectors.joining(" "));
			let type : any = tuple.right();
			return Main.divide(beforeType, arg => Main.foldTypeSeparator(arg)).popLast().flatMap(typeParamDivisionsTuple => {
				let joined : any = typeParamDivisionsTuple.left().stream().collect(Collectors.joining(" "));
				let typeParamsString : any = typeParamDivisionsTuple.right().strip();
				if (typeParamsString.startsWith(" < ") && typeParamsString.endsWith(" > ")){
					let slice : any = typeParamsString.substring(1, typeParamsString.length() - 1);
					let typeParams : any = Main.divideValues(slice).stream().map(arg => String.strip(arg)).filter(value => !value.isEmpty()).toList();
					return Main.assemble(joined, typeParams, name, type);
				}
				else 
					return new None();
			}).or(/*() -> {
                return Main.assemble(beforeType, Collections.emptyList(), name, type);
            }*/);
		}).or(/*() -> {
            return Main.assemble("", Collections.emptyList(), name, beforeName);
        }*/);
	}
	assemble(beforeTypeParams : string, typeParams : List<string>, name : string, type : string) : Optional<Definition> {
		let annotationIndex : any = beforeTypeParams.lastIndexOf('\n');
		if (0 <= annotationIndex){
			let annotationString : any = beforeTypeParams.substring(0, annotationIndex);
			let modifiersString : any = beforeTypeParams.substring(annotationIndex + "\n".length());
			return new Some(new Definition(Main.parseAnnotations(annotationString), Main.parseModifiers(modifiersString), typeParams, name, Main.compileType(type)));
		}
		else {
			let modifiers : any = Main.parseModifiers(beforeTypeParams);
			return new Some(new Definition(Lists.empty(), modifiers, typeParams, name, Main.compileType(type)));
		}
	}
	parseModifiers(joined : string) : JavaList<string> {
		let list : any = Main.divide(joined, Main.foldByDelimiter(' ')).stream().map(arg => String.strip(arg)).filter(value => !value.isEmpty()).collect(Collectors.toCollection(arg => ArrayList.new(arg)));
		return new JavaList(list);
	}
	foldByDelimiter(delimiter : char) : BiFunction<State, Character, State> {/*
        return (state, c) -> {
            if (c == delimiter)
                return state.advance();
            return state.append(c);
        }*//*;*/
	}
	foldTypeSeparator(state : State, c : Character) : State {
		if (' ' == c && state.isLevel())
			return state.advance();
		let appended : any = state.append(c);
		if ('<' == c)
			return appended.enter();
		if ('>' == c)
			return appended.exit();
		return appended;
	}
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
				let list : any = Main.divideValues(argument).stream().map(arg => String.strip(arg)).filter(value => !value.isEmpty()).toList();
				if (list.isEmpty())
					return base;
				else {
					let compiled : any = list.stream().map(arg => Main.compileType(arg)).collect(Collectors.joining(", "));
					return base + " < " + compiled + " > ";
				}
			}
		}
		if (strip.endsWith("[]")){
			let slice : any = strip.substring(0, /* strip.length(*/) - "[]".length(/*)*/);
			let compiled : any = Main.compileType(slice);
			return compiled + "[]";
		}
		if (Main.isSymbol(strip))
			return strip;
		return Placeholder.generate(strip);
	}
	divideValues(argument : CharSequence) : ListLike<string> {
		return Main.divide(argument, arg => Main.foldValues(arg));
	}
	compileValues(input : CharSequence, mapper : Function<string, string>) : string {
		return Main.divideValues(input).stream().map(mapper).collect(Collectors.joining(", "));
	}
	foldValues(state : State, c : char) : State {
		if (',' == c && state.isLevel())
			return state.advance();
		let appended : any = state.append(c);
		if ('-' == c){
			let peek : any = appended.peek();
			if (peek.filter(value => '>' == value).isPresent())
				return appended.popAndAppendToOption().orElse(appended);
		}
		if ('<' == c || '(' == c)
			return appended.enter();
		if ('>' == c || ')' == c)
			return appended.exit();
		return appended;
	}
	parseStructureHeader(input : string) : StructureDefinition {
		return Main.parseStructureHeaderWithKeyword(input, "class", "class").or(/*(*/) -  > Main.parseStructureHeaderWithKeyword(input, "record", /* "class")*/).or(() -  > Main.parseStructureHeaderWithKeyword(input, "interface", "interface")).orElseGet(() -  > new Placeholder(input));
	}
	parseStructureHeaderWithKeyword(input : string, keyword : string, type : string) : Optional<StructureDefinition> {
		let classIndex : any = input.indexOf(keyword + " ");
		if (0 > classIndex)
			return new None();
		let beforeKeyword : any = input.substring(0, classIndex).strip();
		let afterKeyword : any = input.substring(classIndex + (keyword + " ").length()).strip();
		let permitsIndex : any = afterKeyword.indexOf("permits");
		if (0 <= permitsIndex){
			let substring : any = afterKeyword.substring(0, permitsIndex);
			return Main.getStructureDefinitionSome(type, beforeKeyword, substring);
		}
		return Main.getStructureDefinitionSome(type, beforeKeyword, afterKeyword);
	}
	getStructureDefinitionSome(type : string, beforeKeyword : string, afterKeyword : string) : Some<StructureDefinition> {
		let implementsIndex : any = afterKeyword.indexOf("implements ");
		if (0 <= implementsIndex){
			let beforeImplements : any = afterKeyword.substring(0, implementsIndex).strip();
			let afterImplements : any = afterKeyword.substring(implementsIndex + "implements ".length()).strip();
			return new Some(Main.complete(type, beforeKeyword, beforeImplements, new Some(afterImplements)));
		}
		else 
			return new Some(Main.complete(type, beforeKeyword, afterKeyword, new None()));
	}
	complete(type : string, beforeKeyword : string, beforeImplements : string, maybeImplements : Optional<string>) : StructureHeader {
		let strip : any = beforeImplements.strip();
		if (!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1)){
			let withoutEnd : any = strip.substring(0, /* strip.length(*/) - ")".length(/*)*/);
			let contentStart : any = withoutEnd.indexOf('(');
			if (0 <= contentStart){
				let strip1 : any = withoutEnd.substring(0, contentStart).strip();
				let substring : any = withoutEnd.substring(contentStart + "(".length());
				let parameters : ListLike<Parameter> = new JavaList(Main.divideValues(substring).stream().map(arg => Main.parseParameter(arg)).toList());
				return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, strip1, parameters);
			}
		}
		return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, beforeImplements, Lists.empty());
	}
	parseStructureHeaderByAnnotations(type : string, beforeKeyword : string, maybeImplements : Optional<string>, strip1 : string, parameters : ListLike<Parameter>) : StructureHeader {
		let index : any = beforeKeyword.lastIndexOf(System.lineSeparator());
		if (0 <= index){
			let annotations : any = Main.parseAnnotations(beforeKeyword.substring(0, index));
			let substring1 : any = beforeKeyword.substring(index + System.lineSeparator().length());
			return new StructureHeader(type, annotations, substring1, strip1, maybeImplements, parameters);
		}
		return new StructureHeader(type, Lists.empty(), beforeKeyword, strip1, maybeImplements, parameters);
	}
	parseAnnotations(input : string) : ListLike<string> {
		let copy : any = Arrays.stream(Pattern.compile("\\n").split(input.strip())).map(arg => String.strip(arg)).filter(value => !value.isEmpty()).map(value => value.substring(1)).collect(Collectors.toCollection(arg => ArrayList.new(arg)));
		return new JavaList(copy);
	}
	divide(input : CharSequence, folder : BiFunction<State, Character, State>) : ListLike<string> {
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
	fold(state : State, c : char, folder : BiFunction<State, Character, State>) : State {
		return Main.foldSingleQuotes(state, c).or(/*(*/) -  > Main.foldDoubleQuotes(state, /* c)*/).orElseGet(() -  > folder.apply(state, c));
	}
	foldDoubleQuotes(state : State, c : char) : Optional<State> {
		if ('\"' != c)
			return new None();
		let current : any = state.append('\"');/*
        while (true) {
            final var maybeTuple =
                    current.popAndAppendToTuple().flatMap(tuple -> Main.getObjectOptional(tuple, current));
            if (maybeTuple.isEmpty())
                break;
        }*/
		return new Some(current);
	}
	getObjectOptional(tuple : Tuple<State, Character>, current : State) : Optional<State> {
		let left : any = tuple.left();
		let next : any = tuple.right();
		if ('\\' == next)
			return new Some(left.popAndAppendToOption().orElse(current));
		if ('\"' == next)
			return new None();
		return new Some(left);
	}
	foldSingleQuotes(state : State, c : char) : Optional<State> {
		if ('\'' != c)
			return new None();
		return state.append(c).popAndAppendToTuple().flatMap(tuple => {
			if ('\\' == tuple.right())
				return tuple.left().popAndAppendToOption();
			return new Some(tuple.left());
		}).flatMap(arg => State.popAndAppendToOption(arg));
	}
	foldStatements(state : State, c : char) : State {
		let appended : any = state.append(c);
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
	handleInvocationSegments(tuple : Tuple<ListLike<string>, string>, depth : number) : Optional<string> {
		let joined : any = tuple.left().stream().collect(Collectors.joining());
		if (joined.isEmpty() || '(' != joined.charAt(joined.length() - 1))
			return new None();
		let substring : any = joined.substring(0, /* joined.length(*/) - "(".length(/*)*/);
		let argument : any = tuple.right();
		return Main.compileCaller(substring, depth).map(caller => caller + "(" + Main.compileValues(argument, input => Main.compileValueOrPlaceholder(input, depth)) + ")");
	}
	compileCaller(input : string, depth : number) : Optional<string> {
		let strip : any = input.strip();
		if (strip.startsWith("new ")){
			let substring : any = strip.substring("new ".length());
			return new Some("new " + Main.compileType(substring));
		}
		return Main.compileValue(strip, depth);
	}
}

