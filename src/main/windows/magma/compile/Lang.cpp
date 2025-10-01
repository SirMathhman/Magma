struct Lang{};
struct JavaRootSegment permits Invalid, Import, JStructure, Package, Whitespace{};
struct CRootSegment{};
struct JavaStructureSegment permits Invalid, JStructure, Method, Whitespace, Field{};
struct JavaType{};
struct CType{};
struct JStructure extends JavaRootSegment, JavaStructureSegment permits Interface, JClass, Record{};
struct Field(JavaDefinition value) implements JavaStructureSegment{};
struct Generic(String base, List<JavaType> arguments) implements JavaType, CType{};
struct Array(JavaType child) implements JavaType{};
struct JavaDefinition(String name, JavaType type){};
struct Method(JavaDefinition definition, Option<List<JavaDefinition>> params, Option<String> body)
			implements JavaStructureSegment{};
struct Invalid(String value, Option<String> after)
			implements JavaRootSegment, JavaStructureSegment, CRootSegment, JavaType, CType{};
struct JClass(Option<String> modifiers, String name, List<JavaStructureSegment> children)
			implements JStructure{};
struct Interface(Option<String> modifiers, String name, List<JavaStructureSegment> children)
			implements JStructure{};
struct Record(Option<String> modifiers, String name, List<JavaStructureSegment> children)
			implements JStructure{};
struct Structure(String name, ArrayList<CDefinition> fields, Option<String> after) implements CRootSegment{};
struct Whitespace() implements JavaRootSegment, JavaStructureSegment{};
struct JavaRoot(List<JavaRootSegment> children){};
struct CRoot(List<CRootSegment> children){};
struct Import(String value) implements JavaRootSegment{};
struct Package(String value) implements JavaRootSegment{};
struct CDefinition(String name, CType type){};
struct Function(CDefinition definition, List<CDefinition> params, String body, Option<String> after)
			implements CRootSegment{};
struct Identifier(String value) implements JavaType, CType{};
struct Pointer(CType child) implements CType{};
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
		return Tag("struct", Prefix("struct ", Suffix(String("name"), "{};")));
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
		final Rule name = String("name");
		final Rule beforeContent = Strip(Or(Suffix(First(name, "<", Values("typeArguments", Identifier())), ">"), name));
		final Rule children = Statements("children", rule);

		final Rule aClass = First(First(Strip(Or(modifiers, Empty)), type + " ", beforeContent), "{", children);
		return Tag(type, Strip(Suffix(aClass, "}")));
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
		Rule params = Values("params", Or(JDefinition(), Whitespace()));
		final Rule header = Strip(Suffix(Last(Node("definition", JDefinition()), "(", params), ")"));
		final Rule withBody = Suffix(First(header, "{", String("body")), "}");
		return Tag("method", Strip(Or(Suffix(header, ";"), withBody)));
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
		return Tag("identifier", Strip(FilterRule.Identifier(String("value"))));
	*/}
Rule Generic_Lang(Rule type) {/*
		return Tag("generic",
							 Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Values("arguments", type)), ">")));
	*/}
Rule Invalid_Lang() {/*
		return Tag("invalid", Placeholder(String("value")));
	*/}
