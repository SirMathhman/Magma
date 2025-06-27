/*package magma;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.Arrays;*/
/*import java.util.Collections;*/
/*import java.util.Optional;*/
/*import java.util.function.BiFunction;*/
/*import java.util.function.Function;*/
/*import java.util.regex.Pattern;*/
/*import java.util.stream.Collectors;*/
/*public*/class Main {
	/*private static final*/ LINE_SEPARATOR : string = System.lineSeparator(/**/);
	constructor (/**/) {/*
    */}
	/*public static*/ main(/*final String[] args*/) : void {
		/*final*/ root : any = Paths.get(/*".", "src", "java"*/);/*
        try (final var stream = Files.walk(root)) {
            final var sources = stream.filter(path -> path.toString().endsWith(".java")).toList();

            Main.runWithSources(sources, root);
        }*//* catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }*//*
    */}
	/*private static void runWithSources(final Iterable<Path> sources, final Path root) throws IOException {
        for (final var source : sources) {
            final var relative = root.relativize(source.getParent());
            final var input = Files.readString(source);
            final var output = Main.compileRoot(input);
            final var targetParent = Paths.get(".", "src", "node").resolve(relative);
            if (!Files.exists(targetParent))
                Files.createDirectories(targetParent);

            final var fileName = source.getFileName().toString();
            final var separator = fileName.lastIndexOf('.');
            final var name = fileName.substring(0, separator);
            final var target = targetParent.resolve(name + ".ts");
            Files.writeString(target, output);
        }
    }*/
	/*private static*/ compileRoot(/*final CharSequence input*/) : string {
		return Main.compileStatements(/*input, Main::compileRootSegment*/);/*
    */}
	/*private static*/ compileStatements(/*final CharSequence input, final Function<String, String> mapper*/) : string {
		return Main.compileAll(/*input, Main::foldStatements, mapper, ""*/);/*
    */}
	/*private static*/ compileAll(/*final CharSequence input, final BiFunction<State, Character, State> folder,
                                     final Function<String, String> mapper, final CharSequence delimiter*/) : string {
		return Main.divide(/*input, folder*/).stream(/**/).map(/*mapper*/).collect(/*Collectors.joining(delimiter)*/);/*
    */}
	/*private static*/ compileRootSegment(/*final String input*/) : string {
		return Main.compileRootSegmentValue(/*input.strip()*/) + /*Main.LINE_SEPARATOR*/;/*
    */}
	/*private static*/ compileRootSegmentValue(/*final String input*/) : string {
		return Main.compileStructure(/*input*/).orElseGet(/*() -> Placeholder.generate(input)*/);/*
    */}
	/*private static*/ compileStructure(/*final String input*/) : Optional<string> {
		if (/*input.isEmpty() || '}' != input*/.charAt(/*input.length() - 1*/))/*
            return Optional.empty();*/
		/*final*/ withoutEnd : any = input.substring(/*0, input.length() - "}".length()*/);
		/*final*/ contentStart : any = withoutEnd.indexOf(/*'{'*/);
		if (/*0 > contentStart*/)/*
            return Optional.empty();*/
		/*final*/ beforeContent : any = withoutEnd.substring(/*0, contentStart*/);
		/*final*/ content : any = /*withoutEnd.substring(contentStart*/ + "{".length(/*)*/);
		/*final*/ definition : any = Main.parseStructureHeader(/*beforeContent*/);/*
        final String structName;*/
		if (/*definition instanceof final StructureHeader header*/)/* {
            if (header.annotations().contains("Actual"))
                return Optional.of("");

            structName = header.name();
        }*/
		/*else
          */ structName :  = "?";
		return Optional.of(/*definition.generate(*/) + " {" + Main.compileStatements(/*content, input1 -> Main.compileStructureSegment(input1, structName)*/) + /*"}")*/;/*
    */}
	/*private static*/ compileStructureSegment(/*final String input, final CharSequence structName*/) : string {
		/*final*/ strip : any = input.strip(/**/);
		return /*Main.LINE_SEPARATOR*/ + "\t" + Main.compileStructureSegmentValue(/*strip, structName*/);/*
    */}
	/*private static*/ compileStructureSegmentValue(/*final String input, final CharSequence structName*/) : string {
		return Main.compileStatement(/*input, Main::compileAssignment*/).or(/*() -> Main.compileMethod(input, structName)*/).orElseGet(/*() -> Placeholder.generate(input)*/);/*
    */}
	/*private static*/ compileStatement(/*final String input,
                                                     final Function<String, Optional<String>> mapper*/) : Optional<string> {
		if (/*input.isEmpty() || ';' != input*/.charAt(/*input.length() - 1*/))/*
            return Optional.empty();*/
		/*final*/ withoutEnd : any = input.substring(/*0, input.length() - ";".length()*/);
		return /*mapper.apply(withoutEnd).map(result -> result*/ + /*";")*/;/*
    */}
	/*private static*/ compileMethod(/*final String input, final CharSequence structName*/) : Optional<string> {
		if (/*input.isEmpty() || '}' != input*/.charAt(/*input.length() - 1*/))/*
            return Optional.empty();*/
		/*final*/ withoutEnd : any = input.substring(/*0, input.length() - "}".length()*/);
		/*final*/ contentStart : any = withoutEnd.indexOf(/*'{'*/);
		if (/*0 > contentStart*/)/*
            return Optional.empty();*/
		/*final*/ before : any = withoutEnd.substring(/*0, contentStart*/).strip(/**/);
		/*final*/ content : any = /*withoutEnd.substring(contentStart*/ + "{".length(/*)*/);
		if (/*before.isEmpty() || ')' != before*/.charAt(/*before.length() - 1*/))/*
            return Optional.empty();*/
		/*final*/ withoutParamEnd : any = before.substring(/*0, before.length() - ")".length()*/);
		/*final*/ paramStart : any = withoutParamEnd.indexOf(/*'('*/);
		if (/*0 > paramStart*/)/*
            return Optional.empty();*/
		/*final*/ definition : any = withoutParamEnd.substring(/*0, paramStart*/);
		/*final*/ params : any = /*withoutParamEnd.substring(paramStart*/ + "(".length(/*)*/);
		/*final*/ joinedParams : any = "(" + Placeholder.generate(/*params*/) + ")";
		return Optional.of(/*Main.parseMethodHeader(definition, structName).generateWithAfterName(joinedParams*/) + " {" + Main.compileStatements(/*content, Main::compileFunctionSegment*/) + /*"}")*/;/*
    */}
	/*private static*/ compileFunctionSegment(/*final String input*/) : string {
		return Main.compileConditional(/*input*/).or(/*() -> Main.compileStatement(input, Main::compileFunctionStatementValue)*/).map(/*value -> System.lineSeparator(*/) + "\t\t" + /*value).orElseGet(() -> Placeholder*/.generate(/*input)*/);/*
    */}
	/*private static*/ compileFunctionStatementValue(/*final String input*/) : Optional<string> {
		return Main.compileReturn(/*input*/).or(/*() -> Main.compileAssignment(input)*/);/*
    */}
	/*private static*/ compileReturn(/*final String input*/) : Optional<string> {
		/*final*/ strip : any = input.strip(/**/);
		if (strip.startsWith(/*"return "*/))/* {
            final var slice = strip.substring("return ".length());
            return Optional.of("return " + Main.compileValue(slice));
        }*/
		return Optional.empty(/**/);/*
    */}
	/*private static*/ compileConditional(/*final String input*/) : Optional<string> {
		/*final*/ strip : any = input.strip(/**/);
		if (/*!strip*/.startsWith(/*"if"*/))/*
            return Optional.empty();*/
		/*final*/ slice : any = strip.substring(/*"if".length()*/).strip(/**/);
		if (/*slice.isEmpty() || '(' != slice*/.charAt(/*0*/))/*
            return Optional.empty();*/
		/*final*/ substring : any = slice.substring(/*1*/);
		return Main.divide(/*substring, Main::foldConditional*/).popFirst(/**/).flatMap(/*Main::compileConditionalSegments*/);/*
    */}
	/*private static*/ compileConditionalSegments(/*final Tuple<String, ListLike<String>> tuple*/) : Optional<string> {
		/*final*/ substring1 : any = tuple.left(/**/);
		if (/*substring1.isEmpty() || ')' != substring1*/.charAt(/*substring1.length() - 1*/))/*
            return Optional.empty();*/
		/*final*/ condition : any = /*substring1*/.substring(/*0, substring1.length() - 1*/);
		/*final*/ substring2 : any = tuple.right(/**/).stream(/**/).collect(/*Collectors.joining()*/);
		return /*Optional.of("if ("*/ + Main.compileValue(/*condition*/) + ")" + Placeholder.generate(/*substring2)*/);/*
    */}
	/*private static*/ foldConditional(/*final State state, final char c*/) : State {
		/*final*/ appended : any = state.append(/*c*/);
		if (/*'('*/ == c)/*
            return appended.enter();*/
		if (/*')'*/ == c)/* {
            if (appended.isLevel())
                return appended.advance();
            return appended.exit();
        }*/
		return appended;/*
    */}
	/*private static*/ parseMethodHeader(/*final String input, final CharSequence structName*/) : MethodHeader {
		return Main.parseConstructor(/*input, structName*/).orElseGet(/*() -> Main.parseDefinitionOrPlaceholder(input)*/);/*
    */}
	/*private static*/ parseConstructor(/*final String input, final CharSequence structName*/) : Optional<MethodHeader> {
		/*final*/ strip : any = input.strip(/**/);
		/*final*/ index : any = strip.lastIndexOf(/*' '*/);
		if (/*0 <= index*/)/* {
            final var name = strip.substring(index + " ".length()).strip();
            if (name.contentEquals(structName))
                return Optional.of(new Constructor());
        }*/
		return Optional.empty(/**/);/*
    */}
	/*private static*/ compileAssignment(/*final String input*/) : Optional<string> {
		/*final*/ separator : any = input.indexOf(/*'='*/);
		if (/*0 <= separator*/)/* {
            final var before = input.substring(0, separator);
            final var after = input.substring(separator + "=".length());
            return Optional.of(Main.parseDefinitionOrPlaceholder(before).generate() + " = " + Main.compileValue(after));
        }*/
		return Optional.empty(/**/);/*
    */}
	/*private static*/ compileValue(/*final String input*/) : string {
		/*final*/ maybeOperator : any = /*Main.compileOperator(input, "*/ >= ")
                                      .or(() -> Main.compileOperator(input, " == "))
                                      .or(() -> Main.compileOperator(input, " + /*"))*/;
		if (maybeOperator.isPresent(/**/))/*
            return maybeOperator.get();*/
		/*final*/ maybeInvocation : any = Main.compileInvocation(/*input*/);
		if (maybeInvocation.isPresent(/**/))/*
            return maybeInvocation.get();*/
		/*final*/ separator : any = input.lastIndexOf(/*'.'*/);
		if (/*0 <= separator*/)/* {
            final var value = input.substring(0, separator);
            final var property = input.substring(separator + ".".length()).strip();
            if (Main.isSymbol(property))
                return Main.compileValue(value) + "." + property;
        }*/
		/*final*/ strip : any = input.strip(/**/);
		if (Main.isNumber(/*strip*/))/*
            return strip;*/
		if (/*!strip.isEmpty() && '\"'*/ == /*strip.charAt(0) && '\"'*/ == strip.charAt(/*strip.length() - 1*/))/*
            return strip;*/
		if (Main.isSymbol(/*strip*/))/*
            return strip;*/
		return Placeholder.generate(/*strip*/);/*
    */}
	/*private static*/ compileOperator(/*final String input, final String operator*/) : Optional<string> {
		/*final*/ i : any = input.indexOf(/*operator*/);
		if (/*0 <= i*/)/* {
            final var substring = input.substring(0, i);
            final var substring1 = input.substring(i + operator.length());
            return Optional.of(Main.compileValue(substring) + " " + operator + " " + Main.compileValue(substring1));
        }*/
		return Optional.empty(/**/);/*
    */}
	/*private static*/ compileInvocation(/*final String input*/) : Optional<string> {
		/*final*/ strip : any = input.strip(/**/);
		if (/*strip.isEmpty() || ')' != strip*/.charAt(/*strip.length() - 1*/))/*
            return Optional.empty();*/
		/*final*/ withoutEnd : any = strip.substring(/*0, strip.length() - ")".length()*/);
		return Main.divide(/*withoutEnd, Main::foldInvocation*/).popLast(/**/).flatMap(/*Main::handleInvocationSegments*/);/*
    */}
	/*private static*/ foldInvocation(/*final State state, final char c*/) : State {
		/*final*/ appended : any = state.append(/*c*/);
		if (/*'('*/ == c)/* {
            final var entered = appended.enter();
            if (entered.isShallow())
                return entered.advance();
            else
                return entered;
        }*/
		if (/*')'*/ == c)/*
            return appended.exit();*/
		return appended;/*
    */}
	/*private static*/ isSymbol(/*final CharSequence input*/) : boolean {
		/*final*/ length : any = input.length(/**/);
		/*for*/ i : /*(var*/ = 0;/* i < length;*//* i++) {
            final var c = input.charAt(i);
            if (Character.isLetter(c))
                continue;
            return false;
        }*/
		return true;/*
    */}
	/*private static*/ isNumber(/*final CharSequence input*/) : boolean {
		/*final*/ length : any = input.length(/**/);
		/*for*/ i : /*(var*/ = 0;/* i < length;*//* i++) {
            final var c = input.charAt(i);
            if (!Character.isDigit(c))
                return false;
        }*/
		return true;/*
    */}
	/*private static*/ parseDefinitionOrPlaceholder(/*final String input*/) : Assignable {
		/*final*/ strip : any = input.strip(/**/);
		return /*Main.parseDefinition(strip).<Assignable>map*/(/*value -> value*/).orElseGet(/*() -> new Placeholder(strip)*/);/*
    */}
	/*private static*/ parseDefinition(/*final String strip*/) : Optional<Definition> {
		/*final*/ separator : any = strip.lastIndexOf(/*' '*/);
		if (/*0 > separator*/)/*
            return Optional.empty();*/
		/*final*/ beforeName : any = strip.substring(/*0, separator*/);
		/*final*/ name : any = /*strip.substring(separator*/ + " ".length(/*)*/);
		/*final*/ divisions : any = Main.divide(/*beforeName, Main::foldTypeSeparator*/);/*
        return divisions.popLast().flatMap(tuple -> {
            final var beforeType = tuple.left().stream().collect(Collectors.joining(" "));
            final var type = tuple.right();
            return Optional.of(new Definition(beforeType, name, Main.compileType(type)));
        }*//*);*//*
    */}
	/*private static*/ foldTypeSeparator(/*final State state, final Character c*/) : State {
		if (/*' '*/ == /*c && state*/.isLevel(/**/))/*
            return state.advance();*/
		/*final*/ appended : any = state.append(/*c*/);
		if (/*'<'*/ == c)/*
            return appended.enter();*/
		if (/*'>'*/ == c)/*
            return appended.exit();*/
		return appended;/*
    */}
	/*private static*/ compileType(/*final String input*/) : string {
		/*final*/ strip : any = input.strip(/**/);
		if ("var".contentEquals(/*strip*/))/*
            return "any";*/
		if ("String".contentEquals(/*strip*/))/*
            return "string";*/
		if ("int".contentEquals(/*strip*/))/*
            return "number";*/
		if (/*!strip.isEmpty() && '>'*/ == strip.charAt(/*strip.length() - 1*/))/* {
            final var withoutEnd = strip.substring(0, strip.length() - ">".length());
            final var start = withoutEnd.indexOf('<');
            if (0 <= start) {
                final var base = withoutEnd.substring(0, start);
                final var argument = withoutEnd.substring(start + "<".length());
                final var compiled = Main.compileAll(argument, Main::foldValues, Main::compileType, ", ");
                return base + "<" + compiled + ">";
            }
        }*/
		if (Main.isSymbol(/*strip*/))/*
            return strip;*/
		return Placeholder.generate(/*strip*/);/*
    */}
	/*private static*/ foldValues(/*final State state, final char c*/) : State {
		if (/*','*/ == /*c && state*/.isLevel(/**/))/*
            return state.advance();*/
		/*final*/ appended : any = state.append(/*c*/);
		if (/*'<'*/ == c)/*
            return appended.enter();*/
		if (/*'>'*/ == c)/*
            return appended.exit();*/
		return appended;/*
    */}
	/*private static*/ parseStructureHeader(/*final String input*/) : StructureDefinition {
		return Main.parseClassHeader(/*input, "class", "class"*/).or(/*() -> Main.parseClassHeader(input, "record", "class")*/).or(/*() -> Main.parseClassHeader(input, "interface", "interface")*/).orElseGet(/*() -> new Placeholder(input)*/);/*
    */}
	/*private static*/ parseClassHeader(/*final String input, final String keyword,
                                                                  final String type*/) : Optional<StructureDefinition> {
		/*final*/ classIndex : any = /*input.indexOf(keyword*/ + /*" ")*/;
		if (/*0 > classIndex*/)/*
            return Optional.empty();*/
		/*final*/ beforeKeyword : any = input.substring(/*0, classIndex*/).strip(/**/);
		/*final*/ afterKeyword : any = /*input.substring(classIndex*/ + /*(keyword*/ + /*" ").length()).strip()*/;
		/*final*/ implementsIndex : any = afterKeyword.indexOf(/*"implements "*/);
		if (/*0 <= implementsIndex*/)/* {
            final var beforeImplements = afterKeyword.substring(0, implementsIndex).strip();
            final var afterImplements = afterKeyword.substring(implementsIndex + "implements ".length()).strip();
            return Optional.of(Main.complete(type, beforeKeyword, beforeImplements, Optional.of(afterImplements)));
        }*//* else
            return Optional.of(Main.complete(type, beforeKeyword, afterKeyword, Optional.empty()));*//*
    */}
	/*private static*/ complete(/*final String type, final String beforeKeyword,
                                            final String beforeImplements, final Optional<String> maybeImplements*/) : StructureHeader {
		/*final*/ strip : any = beforeImplements.strip(/**/);
		if (/*!strip.isEmpty() && ')'*/ == strip.charAt(/*strip.length() - 1*/))/* {
            final var withoutEnd = strip.substring(0, strip.length() - ")".length());
            final var contentStart = withoutEnd.indexOf('(');
            if (0 <= contentStart) {
                final var strip1 = withoutEnd.substring(0, contentStart).strip();
                return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, strip1);
            }
        }*/
		return Main.parseStructureHeaderByAnnotations(/*type, beforeKeyword, maybeImplements, beforeImplements*/);/*
    */}
	/*private static*/ parseStructureHeaderByAnnotations(/*final String type, final String beforeKeyword,
                                                                     final Optional<String> maybeImplements,
                                                                     final String strip1*/) : StructureHeader {
		/*final*/ index : any = beforeKeyword.lastIndexOf(/*System.lineSeparator()*/);
		if (/*0 <= index*/)/* {
            final var annotations =
                    Arrays.stream(Pattern.compile("\\n").split(beforeKeyword.substring(0, index).strip()))
                          .map(String::strip).filter(value -> !value.isEmpty()).map(value -> value.substring(1))
                          .toList();

            final var substring1 = beforeKeyword.substring(index + System.lineSeparator().length());
            return new StructureHeader(type, annotations, substring1, strip1, maybeImplements);
        }*/
		return /*new StructureHeader*/(/*type, Collections.emptyList(), beforeKeyword, strip1, maybeImplements*/);/*
    */}
	/*private static*/ divide(/*final CharSequence input,
                                           final BiFunction<State, Character, State> foldStatements*/) : ListLike<string> {
		/**/ current : State = /*new MutableState*/(/*input*/);/*
        while (true) {
            final var maybe = current.pop();
            if (maybe.isEmpty())
                break;

            final var tuple = maybe.get();
            current = tuple.left();
            current = Main.fold(current, tuple.right(), foldStatements);
        }*/
		return current.advance(/**/).unwrap(/**/);/*
    */}
	/*private static*/ fold(/*final State state, final char c, final BiFunction<State, Character, State> folder*/) : State {
		return Main.foldSingleQuotes(/*state, c*/).or(/*() -> Main.foldDoubleQuotes(state, c)*/).orElseGet(/*() -> folder.apply(state, c)*/);/*
    */}
	/*private static*/ foldDoubleQuotes(/*final State state, final char c*/) : Optional<State> {
		if (/*'\"' != c*/)/*
            return Optional.empty();*/
		/**/ current : any = state.append(/*'\"'*/);/*
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
		return Optional.of(/*current*/);/*

    */}
	/*private static*/ foldSingleQuotes(/*final State state, final char c*/) : Optional<State> {
		if (/*'\'' != c*/)/*
            return Optional.empty();*/
		return /*state.append(c).popAndAppendToTuple().flatMap(
                            tuple -> '\\'*/ == /*tuple.right() ? tuple.left().popAndAppendToOption() : Optional*/.of(/*tuple.left()))
                    .flatMap(State::popAndAppendToOption*/);/*
    */}
	/*private static*/ foldStatements(/*final State state, final char c*/) : State {
		/*final*/ appended : any = state.append(/*c*/);
		if (/*';'*/ == /*c && appended*/.isLevel(/**/))/*
            return appended.advance();*/
		if (/*'}'*/ == /*c && appended*/.isShallow(/**/))/*
            return appended.exit().advance();*/
		if (/*'{'*/ == c)/*
            return appended.enter();*/
		if (/*'}'*/ == c)/*
            return appended.exit();*/
		return appended;/*
    */}
	/*private static*/ handleInvocationSegments(/*final Tuple<ListLike<String>, String> tuple*/) : Optional<string> {
		/*final*/ caller : any = tuple.left(/**/).stream(/**/).collect(/*Collectors.joining()*/);
		if (/*caller.isEmpty() || '(' != caller*/.charAt(/*caller.length() - 1*/))/*
            return Optional.empty();*/
		/*final*/ substring : any = caller.substring(/*0, caller.length() - "(".length()*/);
		/*final*/ argument : any = tuple.right(/**/);
		return Optional.of(/*Main.compileValue(substring*/) + "(" + Placeholder.generate(/*argument*/) + /*")")*/;/*
    */}
	/**/}
/**/
