// Generated transpiled C++ from 'src\main\java\magma\compile\Lang.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Lang{};
struct JavaType{};
struct CType{};
struct Field{JavaDefinition value;};
struct Generic{char* base;List<JavaType> arguments;};
struct Array{JavaType child;};
struct JavaDefinition{char* name;JavaType type;Option<List<Modifier>> modifiers;Option<List<Identifier>> typeParameters;};
struct Modifier{char* value;};
struct Method{JavaDefinition definition;Option<List<JavaDefinition>> params;Option<List<JFunctionSegment>> body;Option<List<Identifier>> typeParameters;};
struct Invalid{char* value;Option<String> after;};
struct JClass{Option<String> modifiers;char* name;List<JStructureSegment> children;Option<List<Identifier>> typeParameters;Option<JavaType> implementsClause;};
struct Interface{Option<String> modifiers;char* name;List<JStructureSegment> children;Option<List<Identifier>> typeParameters;Option<JavaType> implementsClause;};
struct Record{Option<String> modifiers;char* name;List<JStructureSegment> children;Option<List<Identifier>> typeParameters;Option<List<JavaDefinition>> params;Option<JavaType> implementsClause;};
struct Structure{char* name;List<CDefinition> fields;Option<String> after;Option<List<Identifier>> typeParameters;};
struct Whitespace{};
struct Placeholder{char* value;};
struct JavaRoot{List<JavaRootSegment> children;};
struct CRoot{List<CRootSegment> children;};
struct Import{char* value;};
struct Package{char* value;};
struct CDefinition{char* name;CType type;Option<List<Identifier>> typeParameters;};
struct CFunctionPointerDefinition{char* name;CType returnType;List<CType> paramTypes;};
struct Function{CDefinition definition;List<CParameter> params;List<CFunctionSegment> body;Option<String> after;Option<List<Identifier>> typeParameters;};
struct Identifier{char* value;};
struct Pointer{CType child;};
struct FunctionPointer{CType returnType;List<CType> paramTypes;};
struct LineComment{char* value;};
struct BlockComment{char* value;};
Rule CRoot_Lang() {
	/*return Statements("children", Strip("", Or(CStructure(), Function(), Invalid()), "after"));*/
}
Rule Function_Lang() {
	/*final NodeRule definition = new NodeRule("definition", CDefinition());*/
	/*final Rule params = Values("params", Or(CFunctionPointerDefinition(), CDefinition()));*/
	/*final Rule body = Statements("body", CFunctionSegment());*/
	/*final Rule functionDecl =
				First(Suffix(First(definition, "(", params), ")"), " {", Suffix(body, System.lineSeparator() + "}*/
	/*"));*/
	/*// Add template declaration only if type parameters exist (non-empty list)*/
	/*final Rule templateParams = Values("typeParameters", Prefix("typename ", Identifier()));*/
	/*final Rule templateDecl =
				NonEmptyList("typeParameters", Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())));*/
	/*final Rule maybeTemplate = Or(templateDecl, Empty);*/
	/*return Tag("function", First(maybeTemplate, "", functionDecl));*/
}
Rule CFunctionPointerDefinition_Lang() {
	/*// Generates: returnType (*name)(paramTypes)*/
	/*return Tag("functionPointerDefinition",
							 Suffix(First(Suffix(First(Node("returnType", CType()), " (*", String("name")), ")("),
														"",
														Values("paramTypes", CType())), ")"));*/
}
Rule CDefinition_Lang() {
	/*return Last(Node("type", CType()), " ", new StringRule("name"));*/
}
Rule CType_Lang() {
	/*final LazyRule rule = new LazyRule();*/
	/*// Function pointer: returnType (*)(paramType1, paramType2, ...)*/
	/*final Rule funcPtr =
				Tag("functionPointer", Suffix(First(Node("returnType", rule), " (*)(", Values("paramTypes", rule)), ")"));*/
	/*rule.set(Or(funcPtr, Identifier(), Tag("pointer", Suffix(Node("child", rule), "*")), Generic(rule), Invalid()));*/
	/*return rule;*/
}
Rule CStructure_Lang() {
	/*// For template structs, use plain name without type parameters in the*/
	/*// declaration*/
	/*final Rule plainName = StrippedIdentifier("name");*/
	/*final Rule structPrefix = Prefix("struct ", plainName);*/
	/*final Rule fields = Statements("fields", Suffix(CDefinition(), ";*/
	/*"));*/
	/*final Rule structWithFields = Suffix(First(structPrefix, "{", fields), "}*/
	/*");*/
	/*final Rule structComplete = Suffix(structWithFields, ";*/
	/*");*/
	/*// Add template declaration only if type parameters exist (non-empty list)*/
	/*final Rule templateParams = Values("typeParameters", Prefix("typename ", Identifier()));*/
	/*final Rule templateDecl =
				NonEmptyList("typeParameters", Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())));*/
	/*final Rule maybeTemplate = Or(templateDecl, Empty);*/
	/*return Tag("struct", First(maybeTemplate, "", structComplete));*/
}
Rule JRoot_Lang() {
	/*final Rule segment =
				Or(Namespace("package"), Namespace("import"), Structures(StructureSegment()), BlockComment(), Whitespace());*/
	/*return Statements("children", segment);*/
}
Rule Structures_Lang(Rule structureMember) {
	/*return Or(JStructure("class", structureMember),
							JStructure("interface", structureMember),
							JStructure("record", structureMember));*/
}
Rule Whitespace_Lang() {
	/*return Tag("whitespace", Strip(Empty));*/
}
Rule Namespace_Lang(char* type) {
	/*return Tag(type, Strip(Prefix(type + " ", Suffix(Invalid(), ";*/
	/*"))));*/
}
Rule JStructure_Lang(char* type, Rule rule) {
	/*final Rule modifiers = String("modifiers");*/
	/*final Rule maybeWithTypeArguments = NameWithTypeParameters();*/
	/*final Rule maybeWithParameters =
				Strip(Or(Suffix(First(maybeWithTypeArguments, "(", Parameters()), ")"), maybeWithTypeArguments));*/
	/*final Rule maybeWithParameters1 =
				Or(Last(maybeWithParameters, "extends", Node("extends", JType())), maybeWithParameters);*/
	/*final Rule beforeContent =
				Or(Last(maybeWithParameters1, "implements", Node("implementsClause", JType())), maybeWithParameters1);*/
	/*final Rule children = Statements("children", rule);*/
	/*final Rule beforeContent1 =
				Or(Last(beforeContent, "permits", Delimited("variants", StrippedIdentifier("variant"), ",")), beforeContent);*/
	/*final Rule aClass = First(First(Strip(Or(modifiers, Empty)), type + " ", beforeContent1), "{", children);
		return Tag(type, Strip(Suffix(aClass, "}*/
	/*")));*/
}
Rule NameWithTypeParameters_Lang() {
	/*final Rule name = StrippedIdentifier("name");*/
	/*final Rule withTypeParameters = Suffix(First(name, "<", Values("typeParameters", Identifier())), ">");*/
	/*return Strip(Or(withTypeParameters, name));*/
}
Rule StructureSegment_Lang() {
	/*final LazyRule structureMember = new LazyRule();*/
	/*structureMember.set(Or(Structures(structureMember),
													 Statement(),
													 JMethod(),
													 LineComment(),
													 BlockComment(),
													 Whitespace()));*/
	/*return structureMember;*/
}
Rule BlockComment_Lang() {
	/*return Tag("block-comment", Strip(Prefix("start", Suffix(String("value"), "end*/
	/*"))));*/
}
Rule LineComment_Lang() {
	/*return Tag("line-comment", Strip(Prefix("//", String("value"))));*/
}
Rule Statement_Lang() {
	/*return Tag("statement", Strip(Suffix(Node("value", JDefinition()), ";*/
	/*")));*/
}
Rule JMethod_Lang() {
	/*Rule params = Parameters();*/
	/*final Rule header = Strip(Suffix(Last(Node("definition", JDefinition()), "(", params), ")"));*/
	/*final Rule withBody = Suffix(First(header, "{", Statements("body", JMethodSegment())), "}*/
	/*");*/
	/*return Tag("method", Strip(Or(Suffix(header, ";*/
	/*"), withBody)));*/
}
Rule JMethodSegment_Lang() {
	/*return Or(Whitespace(), Strip(Tag("placeholder", Placeholder(String("value")))));*/
}
Rule CFunctionSegment_Lang() {
	/*return Or(Whitespace(), Prefix(System.lineSeparator() + "\t", Tag("placeholder", Placeholder(String("value")))));*/
}
Rule Parameters_Lang() {
	/*return Values("params", Or(ParameterDefinition(), Whitespace()));*/
}
Rule ParameterDefinition_Lang() {
	/*// Use TypeFolder to properly parse generic types like Function<T, R>*/
	/*// Parameters don't have modifiers, just type and name*/
	/*final FoldingDivider typeDivider = new FoldingDivider(new TypeFolder());*/
	/*final Splitter typeSplitter = KeepLast(typeDivider);*/
	/*return Tag("definition", new SplitRule(Node("type", JType()), String("name"), typeSplitter));*/
}
Rule JDefinition_Lang() {
	/*// Use TypeFolder to properly parse generic types like Function<T, R>*/
	/*// Split into modifiers+type and name using type-aware splitting*/
	/*final Rule type = Node("type", JType());*/
	/*final Rule name = String("name");*/
	/*final Rule typeAndName = Split(type, KeepLast(new FoldingDivider(new TypeFolder())), name);*/
	/*// Handle optional modifiers before type*/
	/*final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");*/
	/*final Rule withModifiers = Split(modifiers, KeepLast(new FoldingDivider(new TypeFolder())), type);*/
	/*Rule beforeName = Or(withModifiers, type);*/
	/*return Tag("definition", Last(beforeName, " ", name));*/
}
Rule JType_Lang() {
	/*final LazyRule type = new LazyRule();*/
	/*type.set(Or(Generic(type), Array(type), Identifier(), Invalid()));*/
	/*return type;*/
}
Rule Array_Lang(Rule type) {
	/*return Tag("array", Strip(Suffix(Node("child", type), "[]")));*/
}
Rule Identifier_Lang() {
	/*return Tag("identifier", StrippedIdentifier("value"));*/
}
Rule StrippedIdentifier_Lang(char* key) {
	/*return Strip(FilterRule.Identifier(String(key)));*/
}
Rule Generic_Lang(Rule type) {
	/*return Tag("generic",
							 Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Values("arguments", type)), ">")));*/
}
Rule Invalid_Lang() {
	/*return Tag("invalid", Placeholder(String("value")));*/
}
