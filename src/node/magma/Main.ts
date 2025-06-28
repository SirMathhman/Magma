






class Main {
	constructor () {
	}
	main() : void {
		sources : /*-> {
            final var*/ = /* files.stream().filter(path -> path.toString().endsWith(".java")).toList();
            return Main.runWithSources(sources, root);
        }, Some::new).ifPresent(Throwable::printStackTrace)*/;
	}
	runWithSources(, root : Path) : Optional<Some[value=]> {
		return /*new None<>()*/;
	}
	runWithSource(, source : Path) : Optional<Some[value=]> {
		return /*JavaFiles.readString(source).match(input -> Main.runWithInput(source, input, relative), Some::new)*/;
	}
	runWithInput(, input : CharSequence, relative : Path) : Optional<Some[value=]> {
		targetParent : any = /* Paths.get(".", "src", "node").resolve(relative)*/;
		return /*Main.extracted1(targetParent).or(() -> Main.compileAndWrite(source, targetParent, output))*/;
	}
	compileAndWrite(, targetParent : Path, output : CharSequence) : Optional<Some[value=]> {
		separator : any = /* fileName.lastIndexOf('.')*/;
		name : any = /* fileName.substring(0, separator)*/;
		target : any = /* targetParent.resolve(name + ".ts")*/;
		return /*JavaFiles.writeString(target, output)*/;
	}
	extracted1() : Optional<Some[value=]> {
		return /*new None<>()*/;
	}
	compileRoot() : string {
	}
	compileStatements(, mapper : Function<Some[value=, string]>) : string {
	}
	compileAll(, folder : BiFunction<Some[value=, Character, State]>, mapper : Function<Some[value=, string]>, delimiter : CharSequence) : string {
	}
	compileRootSegment() : string {
	}
	compileRootSegmentValue() : string {
		return /*Main.compileNamespaced(input).or(() -> Main.compileStructure(input))
                   .orElseGet(() -> Placeholder.generate(input))*/;
	}
	compileNamespaced() : Optional<Some[value=]> {
		if (/*strip.startsWith("package ") || strip.startsWith("import ")*/)
		return /*new None<>()*/;
	}
	compileStructure() : Optional<Some[value=]> {
		withoutEnd : any = /* input.substring(0, input.length() - "}".length())*/;
		contentStart : any = /* withoutEnd.indexOf('{')*/;
		if (0 > contentStart)
		beforeContent : any = /* withoutEnd.substring(0, contentStart)*/;
		content : any = /* withoutEnd.substring(contentStart + "{".length())*/;
		definition : any = /* Main.parseStructureHeader(beforeContent)*/;/*
        final String structName;*//*
        final String joined;*/
		if (/*definition instanceof final StructureHeader header*/)
		else {
			joined = "";
		}
		compiled : any = /*
                Main.compileStatements(content, input1 -> Main.compileStructureSegment(input1, structName))*/;
		return /*new Some<>(definition.generate() + " {" + joined + compiled + Main.LINE_SEPARATOR + "}")*/;
	}
	compileStructureSegment(, structName : CharSequence) : string {
		if (/*strip.isEmpty()*/)
		return /*Main.LINE_SEPARATOR + "\t" + Main.compileStructureSegmentValue(strip, structName)*/;
	}
	compileStructureSegmentValue(, structName : CharSequence) : string {
	}
	compileStatement(, mapper : Function<Some[value=, Optional<Some[value=]>]>) : Optional<Some[value=]> {
		withoutEnd : any = /* input.substring(0, input.length() - ";".length())*/;
		return /*mapper.apply(withoutEnd).map(result -> result + ";")*/;
	}
	compileMethod(, structName : CharSequence) : Optional<Some[value=]> {
		if (0 > paramEnd)
		withParams : any = /* input.substring(0, paramEnd)*/;
		paramStart : any = /* withParams.indexOf('(')*/;
		if (0 > paramStart)
		definition : any = /* withParams.substring(0, paramStart)*/;
		params : any = /* withParams.substring(paramStart + "(".length())*/;
		joinedParams : any = "(" + Main.compileParameters(params) + ")";
		withBraces : any = /* input.substring(paramEnd + ")".length()).strip()*/;
		oldHeader : any = /* Main.parseMethodHeader(definition, structName)*/;
		newHeader : any = /* Main.modifyMethodHeader(oldHeader)*/;/*

        final String outputContent;*/
		if (/*";".contentEquals(withBraces) ||
            (newHeader instanceof final Definition definition1 && definition1.annotations().contains("Actual"))*/)
		else 
			if (/*!withBraces.isEmpty() && '{' == withBraces.charAt(0) &&
                 '}' == withBraces.charAt(withBraces.length() - 1)*/)
		else 
			return /*new None<>()*/;
		return /*new Some<>(newHeader.generateWithAfterName(joinedParams) + outputContent)*/;
	}
	compileParameters() : string {
	}
	modifyMethodHeader() : MethodHeader {/*;*/
	}
	compileFunctionSegments(, depth : number) : string {
	}
	parseParameter() : Parameter {
		return /*Main.parseDefinitionOrPlaceholder(input)*/;
	}
	compileFunctionSegment(, depth : number) : string {
		return /*Main.compileConditional(input, depth).or(() -> Main.compileElse(input, depth))
                   .or(() -> Main.compileStatement(input, input1 -> Main.compileFunctionStatementValue(input1, depth)))
                   .map(value -> System.lineSeparator() + "\t".repeat(depth) + value)
                   .orElseGet(() -> Placeholder.generate(input))*/;
	}
	compileElse(, depth : number) : Optional<Some[value=]> {
		if (/*strip.startsWith("else")*/)
		else 
			return /*new None<>()*/;
	}
	compileFunctionStatementValue(, depth : number) : Optional<Some[value=]> {
	}
	compileReturn(, depth : number) : Optional<Some[value=]> {
		if (/*strip.startsWith("return ")*/)
		return /*new None<>()*/;
	}
	compileConditional(, depth : number) : Optional<Some[value=]> {
		if (/*!strip.startsWith("if")*/)
		slice : any = /* strip.substring("if".length()).strip()*/;
		if (/*slice.isEmpty() || '(' != slice.charAt(0)*/)
		substring : any = /* slice.substring(1)*/;
		return /*Main.divide(substring, Main::foldConditional).popFirst()
                   .flatMap(tuple -> Main.compileConditionalSegments(tuple, depth))*/;
	}
	compileConditionalSegments(, depth : number) : Optional<Some[value=]> {
		if (/*substring1.isEmpty() || ')' != substring1.charAt(substring1.length() - 1)*/)
		condition : any = /* substring1.substring(0, substring1.length() - 1)*/;
		joined : any = /* tuple.right().stream().collect(new Joiner()).orElse("")*/;
		compiled : any = /* Main.compileBlockOrStatement(depth, joined)*/;
		return /*new Some<>("if (" + Main.compileValueOrPlaceholder(condition, depth) + ")" + compiled)*/;
	}
	compileBlockOrStatement(, input : string) : string {
	}
	compileBlock(, input : string) : Optional<Some[value=]> {
		compiled1 : any = /*
                Main.compileFunctionSegments(input.strip().substring(1, input.strip().length() - 1), depth + 1)*/;
		compiled : string = "{" + compiled1 + Main.LINE_SEPARATOR + "\t".repeat(depth) + "}";
		return /*new Some<>(compiled)*/;
	}
	isBlock() : boolean {
	}
	foldConditional(, c : char) : State {
		if ('(' == c)
		if (')' == c)
		return appended;
	}
	parseMethodHeader(, structName : CharSequence) : MethodHeader {
	}
	parseConstructor(, structName : CharSequence) : Optional<Some[value=]> {
		index : any = /* strip.lastIndexOf(' ')*/;
		if (0 <= index)
		return /*new None<>()*/;
	}
	parseAssignment(, depth : number) : Optional<Some[value=]> {
		if (0 <= separator)
		return /*new None<>()*/;
	}
	compileValueOrPlaceholder(, depth : number) : string {
	}
	compileValue(, depth : number) : Optional<Some[value=]> {
		if (/*maybeLambda.isPresent()*/)
		maybeOperator : any = /* Main.compileOperators(input, depth)*/;
		if (/*maybeOperator.isPresent()*/)
		maybeInvocation : any = /* Main.compileInvokable(input, depth)*/;
		if (/*maybeInvocation.isPresent()*/)
		dataSeparator : any = /* input.lastIndexOf('.')*/;
		if (0 <= dataSeparator)
		methodSeparator : any = /* input.lastIndexOf("::")*/;
		if (0 <= methodSeparator)
		strip : any = /* input.strip()*/;
		if (/*!strip.isEmpty() && '!' == strip.charAt(0)*/)
		if (/*Main.isNumber(strip)*/)
		if (/*!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == strip.charAt(strip.length() - 1)*/)
		if (/*Main.isChar(strip)*/)
		if (/*Main.isSymbol(strip)*/)
		return /*new None<>()*/;
	}
	compileLambda(, depth : number) : Optional<Some[value=]> {
		if (0 > arrowIndex)
		before : any = /* input.substring(0, arrowIndex).strip()*/;
		if (/*!Main.isSymbol(before)*/)
		after : any = /* input.substring(arrowIndex + "->".length())*/;
		return /*Main.compileBlock(depth, after).or(() -> Main.compileValue(after, depth))
                   .map(afterResult -> before + " => " + afterResult)*/;
	}
	compileOperators(, depth : number) : Optional<Some[value=]> {
	}
	isChar() : boolean {
	}
	compileOperator(, operator : string, depth : number) : Optional<Some[value=]> {
		if (0 > i)
		leftSlice : any = /* input.substring(0, i)*/;
		rightSlice : any = /* input.substring(i + operator.length())*/;
		return /*Main.compileValue(leftSlice, depth).flatMap(
                left -> Main.compileValue(rightSlice, depth).map(right -> left + " " + operator + " " + right))*/;
	}
	compileInvokable(, depth : number) : Optional<Some[value=]> {
		if (/*strip.isEmpty() || ')' != strip.charAt(strip.length() - 1)*/)
		withoutEnd : any = /* strip.substring(0, strip.length() - ")".length())*/;
		return /*Main.divide(withoutEnd, Main::foldInvocation).popLast()
                   .flatMap(tuple -> Main.handleInvocationSegments(tuple, depth))*/;
	}
	foldInvocation(, c : char) : State {
		if ('(' == c)
		if (')' == c)
		return appended;
	}
	isSymbol() : boolean {/*
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (Main.isSymbolChar(c, i))
                continue;
            return false;
        }*/
		return true;
	}
	isSymbolChar(, i : number) : boolean {
	}
	isNumber() : boolean {/*
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (!Character.isDigit(c))
                return false;
        }*/
		return true;
	}
	parseDefinitionOrPlaceholder() : Assignable {
		return /*Main.parseDefinition(strip).<Assignable>map(value -> {
            final var newModifiers = Lists.<String>empty();
            return new Definition(value.annotations(), newModifiers, value.typeParams(), value.name(), value.type());
        }).orElseGet(() -> new Placeholder(strip))*/;
	}
	parseDefinition() : Optional<Some[value=]> {
		if (0 > separator)
		beforeName : any = /* strip.substring(0, separator)*/;
		name : any = /* strip.substring(separator + " ".length())*/;
		return /*Main.divide(beforeName, Main::foldTypeSeparator).popLast().flatMap(tuple -> {
            final var beforeType = tuple.left().stream().collect(new Joiner(" ")).orElse("");
            final var type = tuple.right();

            return Main.divide(beforeType, Main::foldTypeSeparator).popLast().flatMap(typeParamDivisionsTuple -> {
                final var joined = typeParamDivisionsTuple.left().stream().collect(new Joiner(" ")).orElse("");
                final var typeParamsString = typeParamDivisionsTuple.right().strip();

                if (typeParamsString.startsWith("<") && typeParamsString.endsWith(">")) {
                    final var slice = typeParamsString.substring(1, typeParamsString.length() - 1);
                    final var typeParams =
                            Main.divideValues(slice).stream().map(String::strip).filter(value -> !value.isEmpty())
                                .collect(new ListCollector<String>());

                    return Main.assemble(joined, typeParams, name, type);
                } else
                    return new None<>();
            }).or(() -> {
                return Main.assemble(beforeType, Lists.empty(), name, type);
            });
        }).or(() -> {
            return Main.assemble("", Lists.empty(), name, beforeName);
        })*/;
	}
	assemble(, typeParams : ListLike<Some[value=]>, name : string, type : string) : Optional<Some[value=]> {
		if (0 <= annotationIndex)
		else {
			return /*new Some<>(new Definition(Lists.empty(), modifiers, typeParams, name, Main.compileType(type)))*/;
		}
	}
	parseModifiers() : ListLike<Some[value=]> {
	}
	foldByDelimiter() : BiFunction<Some[value=, Character, State]> {/*;*/
	}
	foldTypeSeparator(, c : Character) : State {
		appended : any = /* state.append(c)*/;
		if ('<' == c)
		if ('>' == c)
		return appended;
	}
	compileType() : string {
		if (/*"var".contentEquals(strip)*/)
		if (/*"String".contentEquals(strip)*/)
		if (/*"int".contentEquals(strip)*/)
		if (/*!strip.isEmpty() && '>' == strip.charAt(strip.length() - 1)*/)
		if (/*strip.endsWith("[]")*/)
		if (/*Main.isSymbol(strip)*/)
		return /*Placeholder.generate(strip)*/;
	}
	divideValues() : ListLike<Some[value=]> {
	}
	compileValues(, mapper : Function<Some[value=, string]>) : string {
	}
	foldValues(, c : char) : State {
		appended : any = /* state.append(c)*/;
		if ('-' == c)
		if ('<' == c || '(' == c)
		if ('>' == c || ')' == c)
		return appended;
	}
	parseStructureHeader() : StructureDefinition {
	}
	parseStructureHeaderWithKeyword(, keyword : string, type : string) : Optional<Some[value=]> {
		if (0 > classIndex)
		beforeKeyword : any = /* input.substring(0, classIndex).strip()*/;
		afterKeyword : any = /* input.substring(classIndex + (keyword + " ").length()).strip()*/;
		permitsIndex : any = /* afterKeyword.indexOf("permits")*/;
		if (0 <= permitsIndex)
		return /*Main.getStructureDefinitionSome(type, beforeKeyword, afterKeyword)*/;
	}
	getStructureDefinitionSome(, beforeKeyword : string, afterKeyword : string) : Some<Some[value=]> {
		if (0 <= implementsIndex)
		else 
			return /*new Some<>(Main.complete(type, beforeKeyword, afterKeyword, new None<>()))*/;
	}
	complete(, beforeKeyword : string, beforeImplements : string, maybeImplements : Optional<Some[value=]>) : StructureHeader {
		if (/*!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1)*/)
		return /*Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, beforeImplements,
                                                      Lists.empty())*/;
	}
	parseStructureHeaderByAnnotations(, beforeKeyword : string, maybeImplements : Optional<Some[value=]>, strip1 : string, parameters : ListLike<Some[value=]>) : StructureHeader {
		if (0 <= index)
		return /*new StructureHeader(type, Lists.empty(), beforeKeyword, strip1, maybeImplements, parameters)*/;
	}
	parseAnnotations() : ListLike<Some[value=]> {
	}
	divide(, folder : BiFunction<Some[value=, Character, State]>) : ListLike<Some[value=]> {/*
        while (true) {
            final var maybe = current.pop().toTuple(new Tuple<>(current, '\0'));
            if (maybe.left()) {
                final var tuple = maybe.right();
                current = tuple.left();
                current = Main.fold(current, tuple.right(), folder);
            } else
                break;
        }*/
		return /*current.advance().unwrap()*/;
	}
	fold(, c : char, folder : BiFunction<Some[value=, Character, State]>) : State {
	}
	foldDoubleQuotes(, c : char) : Optional<Some[value=]> {
		current : any = /* state.append('\"')*/;/*
        while (true) {
            final var maybeTuple =
                    current.popAndAppendToTuple().flatMap(tuple -> Main.getObjectOptional(tuple, current));
            if (maybeTuple.isEmpty())
                break;
        }*/
		return /*new Some<>(current)*/;
	}
	getObjectOptional(, current : State) : Optional<Some[value=]> {
		next : any = /* tuple.right()*/;
		if ('\\' == next)
		if ('\"' == next)
		return /*new Some<>(left)*/;
	}
	foldSingleQuotes(, c : char) : Optional<Some[value=]> {
		return /*state.append(c).popAndAppendToTuple().flatMap(tuple -> {
            if ('\\' == tuple.right())
                return tuple.left().popAndAppendToOption();
            return new Some<>(tuple.left());
        }).flatMap(State::popAndAppendToOption)*/;
	}
	foldStatements(, c : char) : State {
		if (/*';' == c && appended.isLevel()*/)
		if (/*'}' == c && appended.isShallow()*/)
		if ('{' == c || '(' == c)
		if ('}' == c || ')' == c)
		return appended;
	}
	handleInvocationSegments(, depth : number) : Optional<Some[value=]> {
		if (/*joined.isEmpty() || '(' != joined.charAt(joined.length() - 1)*/)
		substring : any = /* joined.substring(0, joined.length() - "(".length())*/;
		argument : any = /* tuple.right()*/;
		return /*Main.compileCaller(substring, depth).map(caller -> caller + "(" + Main.compileValues(argument,
                                                                                                    input -> Main.compileValueOrPlaceholder(
                                                                                                            input,
                                                                                                            depth)) +
                                                                  ")")*/;
	}
	compileCaller(, depth : number) : Optional<Some[value=]> {
		if (/*strip.startsWith("new ")*/)
		return /*Main.compileValue(strip, depth)*/;
	}
}

