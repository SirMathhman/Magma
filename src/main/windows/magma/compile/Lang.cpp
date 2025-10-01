struct Lang<>{};
struct JavaRootSegment<>{};
struct CRootSegment<>{};
struct JavaStructureSegment<>{};
struct JavaType<>{};
struct CType<>{};
struct JStructure<>{};
struct Field<>{};
struct Generic<>{};
struct Array<>{};
struct JavaDefinition<>{};
struct Method<>{};
struct Invalid<>{};
struct JClass<>{};
struct Interface<>{};
struct Record<>{};
struct Structure<>{};
struct Whitespace<>{};
struct JavaRoot<>{};
struct CRoot<>{};
struct Import<>{};
struct Package<>{};
struct CDefinition<>{};
struct Function<>{};
struct Identifier<>{};
struct Pointer<>{};
Rule CRoot_Lang() {/*
		return Statements("children", Strip("", Or(CStructure(), Function(), Invalid()), "after"));
	*/}
Rule Function_Lang() {/*
		final NodeRule definition = new NodeRule("definition", CDefinition());
		final Rule params = Values("params", CDefinition());
		final Rule body = Placeholder(new StringRule("body"));
		return Tag("function", First(Suffix(First(definition, "(", params), ")"), " {", Suffix(body, "}")));
	*/}
Rule CDefinition_Lang() {/*
		return Last(Node("type", CType()), " ", new StringRule("name"));
	*/}
Rule CType_Lang() {/*
		final LazyRule rule = new LazyRule();
		rule.set(Or(Identifier(), Tag("pointer", Suffix(Node("child", rule), "*")), Generic(rule), Invalid()));
		return rule;
	*/}
Rule CStructure_Lang() {/*
		return Tag("struct", Prefix("struct ", Suffix(NameWithTypeParameters(), "{};")));
	*/}
Rule JavaRoot_Lang() {/*
		final Rule segment = Or(Namespace("package"), Namespace("import"), Structures(StructureMember()), Whitespace());
		return Statements("children", segment);
	*/}
Rule Structures_Lang(Rule structureMember) {/*
		return Or(JStructure("class", structureMember),
				JStructure("interface", structureMember),
				JStructure("record", structureMember));
	*/}
Rule Whitespace_Lang() {/*
		return Tag("whitespace", Strip(Empty));
	*/}
Rule Namespace_Lang(char* type) {/*
		return Tag(type, Strip(Prefix(type + " ", Suffix(Invalid(), ";"))));
	*/}
Rule JStructure_Lang(char* type, Rule rule) {/*
		final Rule modifiers = String("modifiers");

		final Rule maybeWithTypeArguments = NameWithTypeParameters();

		final Rule maybeWithParameters = Strip(
				Or(Suffix(First(maybeWithTypeArguments, "(", Parameters()), ")"), maybeWithTypeArguments));

		final Rule maybeWithParameters1 = Or(Last(maybeWithParameters, "extends", Node("extends", JType())),
				maybeWithParameters);

		final Rule beforeContent = Or(Last(maybeWithParameters1, "implements", Node("implements", JType())),
				maybeWithParameters1);

		final Rule children = Statements("children", rule);

		final Rule beforeContent1 = Or(
				Last(beforeContent, " permits ", Delimited("variants", StrippedIdentifier("variant"), ",")), beforeContent);

		final Rule aClass = First(First(Strip(Or(modifiers, Empty)), type + " ", beforeContent1), "{", children);
		return Tag(type, Strip(Suffix(aClass, "}")));
	*/}
Rule NameWithTypeParameters_Lang() {/*
		final Rule name = StrippedIdentifier("name");
		final Rule withTypeParameters = Suffix(First(name, "<", Values("typeParameters", Identifier())), ">");
		return Strip(Or(withTypeParameters, name));
	*/}
Rule StructureMember_Lang() {/*
		final LazyRule structureMember = new LazyRule();
		structureMember.set(Or(Structures(structureMember), Statement(), Method(), Whitespace()));
		return structureMember;
	*/}
Rule Statement_Lang() {/*
		return Tag("statement", Strip(Suffix(Node("value", JDefinition()), ";")));
	*/}
Rule Method_Lang() {/*
		Rule params = Parameters();
		final Rule header = Strip(Suffix(Last(Node("definition", JDefinition()), "(", params), ")"));
		final Rule withBody = Suffix(First(header, "{", String("body")), "}");
		return Tag("method", Strip(Or(Suffix(header, ";"), withBody)));
	*/}
Rule Parameters_Lang() {/*
		return Values("params", Or(JDefinition(), Whitespace()));
	*/}
Rule JDefinition_Lang() {/*
		final Rule modifiers = Delimited("modifiers", Tag("modifier", String("value")), " ");
		final Rule type = Node("type", JType());
		final Rule last = Last(modifiers, " ", type);
		return Tag("definition", Last(Or(last, type), " ", String("name")));
	*/}
Rule JType_Lang() {/*
		final LazyRule type = new LazyRule();
		type.set(Or(Generic(type), Array(type), Identifier(), Invalid()));
		return type;
	*/}
Rule Array_Lang(Rule type) {/*
		return Tag("array", Strip(Suffix(Node("child", type), "[]")));
	*/}
Rule Identifier_Lang() {/*
		return Tag("identifier", StrippedIdentifier("value"));
	*/}
Rule StrippedIdentifier_Lang(char* key) {/*
		return Strip(FilterRule.Identifier(String(key)));
	*/}
Rule Generic_Lang(Rule type) {/*
		return Tag("generic",
				Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Values("arguments", type)), ">")));
	*/}
Rule Invalid_Lang() {/*
		return Tag("invalid", Placeholder(String("value")));
	*/}
