// Generated transpiled C++ from 'src\main\java\magma\compile\Lang.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct Lang{};
template<>
struct JavaType{};
template<>
struct CType{};
template<>
struct Field{JavaDefinition value;};
template<>
struct Generic{char* base;, List<JavaType> arguments;};
template<>
struct Array{JavaType child;};
template<>
struct JavaDefinition{char* name;, JavaType type;, Option<List<Modifier>> modifiers;, Option<List<Identifier>> typeParameters;};
template<>
struct Modifier{char* value;};
template<>
struct Method{JavaDefinition definition;, Option<List<JavaDefinition>> params;, Option<String> body;, Option<List<Identifier>> typeParameters;};
template<>
struct Invalid{char* value;, Option<String> after;};
template<>
struct JClass{Option<String> modifiers;, char* name;, List<JStructureSegment> children;, Option<List<Identifier>> typeParameters;, Option<JavaType> implementsClause;};
template<>
struct Interface{Option<String> modifiers;, char* name;, List<JStructureSegment> children;, Option<List<Identifier>> typeParameters;, Option<JavaType> implementsClause;};
template<>
struct Record{Option<String> modifiers;, char* name;, List<JStructureSegment> children;, Option<List<Identifier>> typeParameters;, Option<List<JavaDefinition>> params;, Option<JavaType> implementsClause;};
template<>
struct Structure{char* name;, List<CDefinition> fields;, Option<String> after;, Option<List<Identifier>> typeParameters;};
template<>
struct Whitespace{};
template<>
struct JavaRoot{List<JavaRootSegment> children;};
template<>
struct CRoot{List<CRootSegment> children;};
template<>
struct Import{char* value;};
template<>
struct Package{char* value;};
template<>
struct CDefinition{char* name;, CType type;, Option<List<Identifier>> typeParameters;};
template<>
struct CFunctionPointerDefinition{char* name;, CType returnType;, List<CType> paramTypes;};
template<>
struct Function{CDefinition definition;, List<CParameter> params;, char* body;, Option<String> after;, Option<List<Identifier>> typeParameters;};
template<>
struct Identifier{char* value;};
template<>
struct Pointer{CType child;};
template<>
struct FunctionPointer{CType returnType;, List<CType> paramTypes;};
template<>
struct LineComment{char* value;};
template<>
struct BlockComment{char* value;};
template<>
/*public static Rule*/ CRoot_Lang() {/*
		return Statements("children", Strip("", Or(CStructure(), Function(), Invalid()), "after"));
	*/}
template<>
/*public static Rule*/ Function_Lang() {/*
		final NodeRule definition = new NodeRule("definition", CDefinition());
		final Rule params = Values("params", Or(CFunctionPointerDefinition(), CDefinition()));
		final Rule body = Placeholder(new StringRule("body"));
		final Rule functionDecl = First(Suffix(First(definition, "(", params), ")"), " {", Suffix(body, "}"));

		// Add template declaration if type parameters exist
		final Rule templateParams = Values("typeParameters", Prefix("typename ", Identifier()));
		final Rule templateDecl = Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator()));
		final Rule maybeTemplate = Or(templateDecl, new StringRule(""));

		return Tag("function", First(maybeTemplate, "", functionDecl));
	*/}
template<>
/*private static Rule*/ CFunctionPointerDefinition_Lang() {/*
		// Generates: returnType (*name)(paramTypes)
		return Tag("functionPointerDefinition",
							 Suffix(First(Suffix(First(Node("returnType", CType()), " (*", String("name")), ")("),
														"",
														Values("paramTypes", CType())), ")"));
	*/}
template<>
/*private static Rule*/ CDefinition_Lang() {/*
		return Last(Node("type", CType()), " ", new StringRule("name"));
	*/}
template<>
/*private static Rule*/ CType_Lang() {/*
		final LazyRule rule = new LazyRule();
		// Function pointer: returnType (*)(paramType1, paramType2, ...)
		final Rule funcPtr =
				Tag("functionPointer", Suffix(First(Node("returnType", rule), " (*)(", Values("paramTypes", rule)), ")"));
		rule.set(Or(funcPtr, Identifier(), Tag("pointer", Suffix(Node("child", rule), "*")), Generic(rule), Invalid()));
		return rule;
	*/}
template<>
/*private static Rule*/ CStructure_Lang() {/*
		// For template structs, use plain name without type parameters in the
		// declaration
		final Rule plainName = StrippedIdentifier("name");
		final Rule structPrefix = Prefix("struct ", plainName);
		final Rule fields = Values("fields", Suffix(CDefinition(), ";"));
		final Rule structWithFields = Suffix(First(structPrefix, "{", fields), "}");
		final Rule structComplete = Suffix(structWithFields, ";");

		// Add template declaration if type parameters exist
		final Rule templateParams = Values("typeParameters", Prefix("typename ", Identifier()));
		final Rule templateDecl = Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator()));
		final Rule maybeTemplate = Or(templateDecl, new StringRule(""));

		return Tag("struct", First(maybeTemplate, "", structComplete));
	*/}
template<>
/*public static Rule*/ JRoot_Lang() {/*
		final Rule segment = Or(Namespace("package"), Namespace("import"), Structures(StructureSegment()),BlockComment(),  Whitespace());
		return Statements("children", segment);
	*/}
template<>
/*private static Rule*/ Structures_Lang(Rule structureMember) {/*
		return Or(JStructure("class", structureMember),
							JStructure("interface", structureMember),
							JStructure("record", structureMember));
	*/}
template<>
/*private static Rule*/ Whitespace_Lang() {/*
		return Tag("whitespace", Strip(Empty));
	*/}
template<>
/*private static Rule*/ Namespace_Lang(char* type) {/*
		return Tag(type, Strip(Prefix(type + " ", Suffix(Invalid(), ";"))));
	*/}
template<>
/*private static Rule*/ JStructure_Lang(char* type, Rule rule) {/*
		final Rule modifiers = String("modifiers");

		final Rule maybeWithTypeArguments = NameWithTypeParameters();

		final Rule maybeWithParameters =
				Strip(Or(Suffix(First(maybeWithTypeArguments, "(", Parameters()), ")"), maybeWithTypeArguments));

		final Rule maybeWithParameters1 =
				Or(Last(maybeWithParameters, "extends", Node("extends", JType())), maybeWithParameters);

		final Rule beforeContent =
				Or(Last(maybeWithParameters1, "implements", Node("implementsClause", JType())), maybeWithParameters1);

		final Rule children = Statements("children", rule);

		final Rule beforeContent1 =
				Or(Last(beforeContent, "permits", Delimited("variants", StrippedIdentifier("variant"), ",")), beforeContent);

		final Rule aClass = First(First(Strip(Or(modifiers, Empty)), type + " ", beforeContent1), "{", children);
		return Tag(type, Strip(Suffix(aClass, "}")));
	*/}
template<>
/*private static Rule*/ NameWithTypeParameters_Lang() {/*
		final Rule name = StrippedIdentifier("name");
		final Rule withTypeParameters = Suffix(First(name, "<", Values("typeParameters", Identifier())), ">");
		return Strip(Or(withTypeParameters, name));
	*/}
template<>
/*private static Rule*/ StructureSegment_Lang() {/*
		final LazyRule structureMember = new LazyRule();
		structureMember.set(Or(Structures(structureMember),
													 Statement(),
													 Method(),
													 LineComment(),
													 BlockComment(),
													 Whitespace()));
		return structureMember;
	*/}
template<>
/*private static Rule*/ BlockComment_Lang() {/*
		return Tag("block-comment", Strip(Prefix("start", Suffix(String("value"), "end"))));
	*/}
template<>
/*private static Rule*/ LineComment_Lang() {/*
		return Tag("line-comment", Strip(Prefix("//", String("value"))));
	*/}
template<>
/*private static Rule*/ Statement_Lang() {/*
		return Tag("statement", Strip(Suffix(Node("value", JDefinition()), ";")));
	*/}
template<>
/*private static Rule*/ Method_Lang() {/*
		Rule params = Parameters();
		final Rule header = Strip(Suffix(Last(Node("definition", JDefinition()), "(", params), ")"));
		final Rule withBody = Suffix(First(header, "{", String("body")), "}");
		return Tag("method", Strip(Or(Suffix(header, ";"), withBody)));
	*/}
template<>
/*private static Rule*/ Parameters_Lang() {/*
		return Values("params", Or(ParameterDefinition(), Whitespace()));
	*/}
template<>
/*private static Rule*/ ParameterDefinition_Lang() {/*
		// Use TypeFolder to properly parse generic types like Function<T, R>
		// Parameters don't have modifiers, just type and name
		final FoldingDivider typeDivider = new FoldingDivider(new TypeFolder());
		final Splitter typeSplitter = DividingSplitter.keepLast(typeDivider);

		return Tag("definition",
							 new SplitRule(Node("type", JType()), String("name"), typeSplitter, "Could not parse parameter", " "));
	*/}
template<>
/*private static Rule*/ JDefinition_Lang() {/*
		// Use TypeFolder to properly parse generic types like Function<T, R>
		final FoldingDivider typeDivider = new FoldingDivider(new TypeFolder());
		final Splitter typeSplitter = DividingSplitter.keepLast(typeDivider);

		// Split into modifiers+type and name using type-aware splitting
		final Rule typeAndName =
				new SplitRule(Node("type", JType()), String("name"), typeSplitter, "Could not parse definition", " ");

		// Handle optional modifiers before type
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		final Rule withModifiers = Last(modifiers, " ", typeAndName);

		return Tag("definition", Or(withModifiers, typeAndName));
	*/}
template<>
/*private static Rule*/ JType_Lang() {/*
		final LazyRule type = new LazyRule();
		type.set(Or(Generic(type), Array(type), Identifier(), Invalid()));
		return type;
	*/}
template<>
/*private static Rule*/ Array_Lang(Rule type) {/*
		return Tag("array", Strip(Suffix(Node("child", type), "[]")));
	*/}
template<>
/*private static Rule*/ Identifier_Lang() {/*
		return Tag("identifier", StrippedIdentifier("value"));
	*/}
template<>
/*private static Rule*/ StrippedIdentifier_Lang(char* key) {/*
		return Strip(FilterRule.Identifier(String(key)));
	*/}
template<>
/*private static Rule*/ Generic_Lang(Rule type) {/*
		return Tag("generic",
							 Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Values("arguments", type)), ">")));
	*/}
template<>
/*private static Rule*/ Invalid_Lang() {/*
		return Tag("invalid", Placeholder(String("value")));
	*/}
