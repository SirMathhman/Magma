struct Lang{};
struct JavaRootSegment permits Content, Import, JStructure, Package, Whitespace{};
struct CRootSegment{};
struct JavaStructureSegment permits Content, JStructure, Method, Whitespace, Field{};
struct JavaType{};
struct CType{};
struct JStructure extends JavaRootSegment, JavaStructureSegment permits Interface, JClass, Record{};
struct Field(JavaDefinition value) implements JavaStructureSegment{};
struct Generic(String base, List<JavaType> arguments) implements JavaType{};
struct JavaDefinition(String name, JavaType type){};
struct Method(JavaDefinition definition, Option<List<JavaDefinition>> params, Option<String> body)
			implements JavaStructureSegment{};
struct Content(String value, Option<String> after)
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
Rule CRoot_Lang() {}
Rule Function_Lang() {}
Rule CDefinition_Lang() {}
Rule CType_Lang() {}
Rule CStructure_Lang() {}
Rule JavaRoot_Lang() {}
Rule Structures_Lang(Rule structureMember) {}
Rule Whitespace_Lang() {}
Rule Namespace_Lang(String type) {}
Rule JStructure_Lang(String type,  Rule rule) {}
Rule StructureMember_Lang() {}
Rule Statement_Lang() {}
Rule Method_Lang() {}
Rule JDefinition_Lang() {}
Rule JavaType_Lang() {}
Rule Identifier_Lang() {}
Rule Generic_Lang() {}
Rule Content_Lang() {}
