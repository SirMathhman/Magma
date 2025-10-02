// Generated transpiled C++ from 'src\main\java\magma\compile\Lang.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Lang{};
struct JavaType{};
struct CType{};
struct Field{JavaDefinition value;};
struct Generic{char* base;, List<JavaType> arguments;};
struct Array{JavaType child;};
struct JavaDefinition{char* name;, JavaType type;, Option<List<Modifier>> modifiers;, Option<List<Identifier>> typeParameters;};
struct Modifier{char* value;};
struct Method{JavaDefinition definition;, Option<List<JavaDefinition>> params;, Option<List<JFunctionSegment>> body;, Option<List<Identifier>> typeParameters;};
struct Invalid{char* value;, Option<String> after;};
struct JClass{Option<String> modifiers;, char* name;, List<JStructureSegment> children;, Option<List<Identifier>> typeParameters;, Option<JavaType> implementsClause;};
struct Interface{Option<String> modifiers;, char* name;, List<JStructureSegment> children;, Option<List<Identifier>> typeParameters;, Option<JavaType> implementsClause;};
struct Record{Option<String> modifiers;, char* name;, List<JStructureSegment> children;, Option<List<Identifier>> typeParameters;, Option<List<JavaDefinition>> params;, Option<JavaType> implementsClause;};
struct Structure{char* name;, List<CDefinition> fields;, Option<String> after;, Option<List<Identifier>> typeParameters;};
struct Whitespace{};
struct JavaRoot{List<JavaRootSegment> children;};
struct CRoot{List<CRootSegment> children;};
struct Import{char* value;};
struct Package{char* value;};
struct CDefinition{char* name;, CType type;, Option<List<Identifier>> typeParameters;};
struct CFunctionPointerDefinition{char* name;, CType returnType;, List<CType> paramTypes;};
struct Function{CDefinition definition;, List<CParameter> params;, char* body;, Option<String> after;, Option<List<Identifier>> typeParameters;};
struct Identifier{char* value;};
struct Pointer{CType child;};
struct FunctionPointer{CType returnType;, List<CType> paramTypes;};
struct LineComment{char* value;};
struct BlockComment{char* value;};
Rule CRoot_Lang() {}
Rule Function_Lang() {}
Rule CFunctionPointerDefinition_Lang() {}
Rule CDefinition_Lang() {}
Rule CType_Lang() {}
Rule CStructure_Lang() {}
Rule JRoot_Lang() {}
Rule Structures_Lang(Rule structureMember) {}
Rule Whitespace_Lang() {}
Rule Namespace_Lang(char* type) {}
Rule JStructure_Lang(char* type, Rule rule) {}
Rule NameWithTypeParameters_Lang() {}
Rule StructureSegment_Lang() {}
Rule BlockComment_Lang() {}
Rule LineComment_Lang() {}
Rule Statement_Lang() {}
Rule Method_Lang() {}
Rule JFunctionSegment_Lang() {}
Rule Parameters_Lang() {}
Rule ParameterDefinition_Lang() {}
Rule JDefinition_Lang() {}
Rule JType_Lang() {}
Rule Array_Lang(Rule type) {}
Rule Identifier_Lang() {}
Rule StrippedIdentifier_Lang(char* key) {}
Rule Generic_Lang(Rule type) {}
Rule Invalid_Lang() {}
