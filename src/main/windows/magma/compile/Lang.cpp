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
struct Generic{char* base;, ListJavaType arguments;};
template<>
struct Array{JavaType child;};
template<>
struct JavaDefinition{char* name;, JavaType type;, OptionListModifier modifiers;, OptionListIdentifier typeParameters;};
template<>
struct Modifier{char* value;};
template<>
struct Method{JavaDefinition definition;, OptionListJavaDefinition params;, OptionString body;, OptionListIdentifier typeParameters;};
template<>
struct Invalid{char* value;, OptionString after;};
template<>
struct JClass{OptionString modifiers;, char* name;, ListJavaStructureSegment children;, OptionListIdentifier typeParameters;, OptionJavaType implementsClause;};
template<>
struct Interface{OptionString modifiers;, char* name;, ListJavaStructureSegment children;, OptionListIdentifier typeParameters;, OptionJavaType implementsClause;};
template<>
struct Record{OptionString modifiers;, char* name;, ListJavaStructureSegment children;, OptionListIdentifier typeParameters;, OptionListJavaDefinition params;, OptionJavaType implementsClause;};
template<>
struct Structure{char* name;, ListCDefinition fields;, OptionString after;, OptionListIdentifier typeParameters;};
template<>
struct Whitespace{};
template<>
struct JavaRoot{ListJavaRootSegment children;};
template<>
struct CRoot{ListCRootSegment children;};
template<>
struct Import{char* value;};
template<>
struct Package{char* value;};
template<>
struct CDefinition{char* name;, CType type;, OptionListIdentifier typeParameters;};
template<>
struct Function{CDefinition definition;, ListCDefinition params;, char* body;, OptionString after;, OptionListIdentifier typeParameters;};
template<>
struct Identifier{char* value;};
template<>
struct Pointer{CType child;};
template<>
Rule CRoot_Lang() {/*
		return Statements("children", Strip("", Or(CStructure(), Function(), Invalid()), "after"));
	*/}
template<>
Rule Function_Lang() {/*
		final NodeRule definition = new NodeRule("definition", CDefinition());
		final Rule params = Values("params", CDefinition());
		final Rule body = Placeholder(new StringRule("body"));
		final Rule functionDecl = First(Suffix(First(definition, "(", params), ")"), " {", Suffix(body, "}"));

		// Add template declaration if type parameters exist
		final Rule templateParams = Values("typeParameters", Prefix("typename ", Identifier()));
		final Rule templateDecl = Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator()));
		final Rule maybeTemplate = Or(templateDecl, new StringRule(""));

		return Tag("function", First(maybeTemplate, "", functionDecl));
	*/}
template<>
Rule CDefinition_Lang() {/*
		return Last(Node("type", CType()), " ", new StringRule("name"));
	*/}
template<>
Rule CType_Lang() {/*
		final LazyRule rule = new LazyRule();
		rule.set(Or(Identifier(), Tag("pointer", Suffix(Node("child", rule), "*")), Generic(rule), Invalid()));
		return rule;
	*/}
template<>
Rule CStructure_Lang() {/*
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
Rule JavaRoot_Lang() {/*
		final Rule segment = Or(Namespace("package"), Namespace("import"), Structures(StructureMember()), Whitespace());
		return Statements("children", segment);
	*/}
template<>
Rule Structures_Lang(Rule structureMember) {/*
		return Or(JStructure("class", structureMember),
				JStructure("interface", structureMember),
				JStructure("record", structureMember));
	*/}
template<>
Rule Whitespace_Lang() {/*
		return Tag("whitespace", Strip(Empty));
	*/}
template<>
Rule Namespace_Lang(char* type) {/*
		return Tag(type, Strip(Prefix(type + " ", Suffix(Invalid(), ";"))));
	*/}
template<>
Rule JStructure_Lang(char* type, Rule rule) {/*
		final Rule modifiers = String("modifiers");

		final Rule maybeWithTypeArguments = NameWithTypeParameters();

		final Rule maybeWithParameters = Strip(
				Or(Suffix(First(maybeWithTypeArguments, "(", Parameters()), ")"), maybeWithTypeArguments));

		final Rule maybeWithParameters1 = Or(Last(maybeWithParameters, "extends", Node("extends", JType())),
				maybeWithParameters);

		final Rule beforeContent = Or(Last(maybeWithParameters1, "implements", Node("implementsClause", JType())),
				maybeWithParameters1);

		final Rule children = Statements("children", rule);

		final Rule beforeContent1 = Or(
				Last(beforeContent, " permits ", Delimited("variants", StrippedIdentifier("variant"), ",")), beforeContent);

		final Rule aClass = First(First(Strip(Or(modifiers, Empty)), type + " ", beforeContent1), "{", children);
		return Tag(type, Strip(Suffix(aClass, "}")));
	*/}
template<>
Rule NameWithTypeParameters_Lang() {/*
		final Rule name = StrippedIdentifier("name");
		final Rule withTypeParameters = Suffix(First(name, "<", Values("typeParameters", Identifier())), ">");
		return Strip(Or(withTypeParameters, name));
	*/}
template<>
Rule StructureMember_Lang() {/*
		final LazyRule structureMember = new LazyRule();
		structureMember.set(Or(Structures(structureMember), Statement(), Method(), Whitespace()));
		return structureMember;
	*/}
template<>
Rule Statement_Lang() {/*
		return Tag("statement", Strip(Suffix(Node("value", JDefinition()), ";")));
	*/}
template<>
Rule Method_Lang() {/*
		Rule params = Parameters();
		final Rule header = Strip(Suffix(Last(Node("definition", JDefinition()), "(", params), ")"));
		final Rule withBody = Suffix(First(header, "{", String("body")), "}");
		return Tag("method", Strip(Or(Suffix(header, ";"), withBody)));
	*/}
template<>
Rule Parameters_Lang() {/*
		return Values("params", Or(JDefinition(), Whitespace()));
	*/}
template<>
Rule JDefinition_Lang() {/*
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		final Rule type = Node("type", JType());
		final Rule last = Last(modifiers, " ", type);
		return Tag("definition", Last(Or(last, type), " ", String("name")));
	*/}
template<>
Rule JType_Lang() {/*
		final LazyRule type = new LazyRule();
		type.set(Or(Generic(type), Array(type), Identifier(), Invalid()));
		return type;
	*/}
template<>
Rule Array_Lang(Rule type) {/*
		return Tag("array", Strip(Suffix(Node("child", type), "[]")));
	*/}
template<>
Rule Identifier_Lang() {/*
		return Tag("identifier", StrippedIdentifier("value"));
	*/}
template<>
Rule StrippedIdentifier_Lang(char* key) {/*
		return Strip(FilterRule.Identifier(String(key)));
	*/}
template<>
Rule Generic_Lang(Rule type) {/*
		return Tag("generic",
				Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Values("arguments", type)), ">")));
	*/}
template<>
Rule Invalid_Lang() {/*
		return Tag("invalid", Placeholder(String("value")));
	*/}
