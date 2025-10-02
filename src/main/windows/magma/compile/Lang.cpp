// Generated transpiled C++ from 'src\main\java\magma\compile\Lang.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Lang {};
struct JavaRootSegment {};
struct CRootSegment {Option<String> after();};
struct JStructureSegment {};
struct JExpression {};
struct JMethodSegment {};
struct CFunctionSegment {};
struct JavaType {};
struct CType {};
struct JStructure {Option<String> modifiers();char* name();Option<List<Identifier>> typeParameters();List<JStructureSegment> children();};
struct CParameter {};
struct CExpression {};
struct CAssignment {CExpression location;CExpression value;};
struct CPostFix {CExpression value;};
struct JAssignment {JExpression location;JExpression value;};
struct JPostFix {JExpression value;};
struct JInitialization {JDefinition definition;JExpression value;};
struct CInitialization {CDefinition definition;CExpression value;};
struct CBlock {List<CFunctionSegment> children;};
struct JBlock {List<JMethodSegment> children;};
struct JIf {JExpression condition;JMethodSegment body;};
struct CIf {CExpression condition;CFunctionSegment body;};
struct JWhile {JExpression condition;JMethodSegment body;};
struct CWhile {CExpression condition;CFunctionSegment body;};
struct Field {JDefinition value;};
struct Generic {char* base;List<JavaType> arguments;};
struct Array {JavaType child;};
struct JDefinition {char* name;JavaType type;Option<List<Modifier>> modifiers;Option<List<Identifier>> typeParameters;};
struct Modifier {char* value;};
struct Method {JDefinition definition;Option<List<JDefinition>> params;Option<List<JMethodSegment>> body;Option<List<Identifier>> typeParameters;};
struct Invalid {char* value;Option<String> after;};
struct JClass {Option<String> modifiers;char* name;List<JStructureSegment> children;Option<List<Identifier>> typeParameters;Option<JavaType> implementsClause;};
struct Interface {Option<String> modifiers;char* name;List<JStructureSegment> children;Option<List<Identifier>> typeParameters;Option<JavaType> implementsClause;Option<JavaType> extendsClause;Option<List<JavaType>> variants;};
struct Record {Option<String> modifiers;char* name;List<JStructureSegment> children;Option<List<Identifier>> typeParameters;Option<List<JDefinition>> params;Option<JavaType> implementsClause;};
struct Structure {char* name;List<CDefinition> fields;Option<String> after;Option<List<Identifier>> typeParameters;};
struct Whitespace {};
struct Placeholder {char* value;};
struct JavaRoot {List<JavaRootSegment> children;};
struct CRoot {List<CRootSegment> children;};
struct Import {char* value;};
struct Package {char* value;};
struct CDefinition {char* name;CType type;Option<List<Identifier>> typeParameters;};
struct CFunctionPointerDefinition {char* name;CType returnType;List<CType> paramTypes;};
struct Function {CDefinition definition;List<CParameter> params;List<CFunctionSegment> body;Option<String> after;Option<List<Identifier>> typeParameters;};
struct Identifier {char* value;};
struct Pointer {CType child;};
struct FunctionPointer {CType returnType;List<CType> paramTypes;};
struct LineComment {char* value;};
struct BlockComment {char* value;};
struct JReturn {JExpression value;};
struct CReturn {CExpression value;};
struct JElse {JMethodSegment child;};
struct CElse {CFunctionSegment child;};
struct JInvokable {JExpression caller;List<JExpression> arguments;};
struct CInvokable {CExpression caller;List<CExpression> arguments;};
struct Break {};
Rule CRoot_Lang() {
	return /*Statements("children", Strip("", Or(CStructure(), Function(), Invalid()), "after"))*/;
}
Rule Function_Lang() {
	/*final NodeRule definition = new NodeRule*/(/*"definition"*/, /* CDefinition())*/;
	/*final Rule params = Arguments*/(/*"params"*/, /* Or(CFunctionPointerDefinition()*/, /* CDefinition()))*/;
	/*final Rule body = Statements*/(/*"body"*/, /* CFunctionSegment())*/;
	/*final Rule functionDecl =
				First*/(/*Suffix(First(definition*/, /* "("*/, /* params)*/, /* ")")*/, /* " {"*/, /* Suffix(body*/, /* System.lineSeparator() + "}"))*/;
	// Add template declaration only if type parameters exist (non-empty list)
	/*final Rule templateParams = Arguments*/(/*"typeParameters"*/, /* Prefix("typename "*/, /* Identifier()))*/;
	/*final Rule templateDecl =
				NonEmptyList*/(/*"typeParameters"*/, /* Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())))*/;
	/*final Rule maybeTemplate = Or*/(/*templateDecl*/, /* Empty)*/;
	return /*Tag("function", First(maybeTemplate, "", functionDecl))*/;
}
Rule CFunctionPointerDefinition_Lang() {
	// Generates: returnType (*name)(paramTypes)
	return /*Tag("functionPointerDefinition",
							 Suffix(First(Suffix(First(Node("returnType", CType()), " (*", String("name")), ")("),
														"",
														Arguments("paramTypes", CType())), ")"))*/;
}
Rule CDefinition_Lang() {
	return /*Last(Node("type", CType()), " ", new StringRule("name"))*/;
}
Rule CType_Lang() {
	/*final LazyRule rule = new LazyRule*/(/*)*/;
	// Function pointer: returnType (*)(paramType1, paramType2, ...)
	/*final Rule funcPtr =
				Tag*/(/*"functionPointer"*/, /* Suffix(First(Node("returnType"*/, /* rule)*/, /* " (*)("*/, /* Arguments("paramTypes"*/, /* rule))*/, /* ")"))*/;
	/*rule.set*/(/*Or(funcPtr*/, /* Identifier()*/, /* Tag("pointer"*/, /* Suffix(Node("child"*/, /* rule)*/, /* "*"))*/, /* Generic(rule)*/, /* Invalid()))*/;
	return /*rule*/;
}
Rule CStructure_Lang() {
	// For template structs, use plain name without type parameters in the
	// declaration
	/*final Rule plainName = StrippedIdentifier*/(/*"name")*/;
	/*final Rule structPrefix = Prefix*/(/*"struct "*/, /* plainName)*/;
	/*final Rule fields = Statements*/(/*"fields"*/, /* Suffix(CDefinition()*/, /* ";"))*/;
	/*final Rule structWithFields = Suffix*/(/*First(structPrefix*/, /* " {"*/, /* fields)*/, /* "}")*/;
	/*final Rule structComplete = Suffix*/(/*structWithFields*/, /* ";")*/;
	// Add template declaration only if type parameters exist (non-empty list)
	/*final Rule templateParams = Arguments*/(/*"typeParameters"*/, /* Prefix("typename "*/, /* Identifier()))*/;
	/*final Rule templateDecl =
				NonEmptyList*/(/*"typeParameters"*/, /* Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())))*/;
	/*final Rule maybeTemplate = Or*/(/*templateDecl*/, /* Empty)*/;
	return /*Tag("struct", First(maybeTemplate, "", structComplete))*/;
}
Rule JRoot_Lang() {
	/*final Rule segment =
				Or*/(/*Namespace("package")*/, /* Namespace("import")*/, /* Structures(StructureSegment())*/, /* BlockComment()*/, /* Whitespace())*/;
	return /*Statements("children", segment)*/;
}
Rule Structures_Lang(Rule structureMember) {
	return /*Or(JStructure("class", structureMember),
							JStructure("interface", structureMember),
							JStructure("record", structureMember))*/;
}
Rule Whitespace_Lang() {
	return /*Tag("whitespace", Strip(Empty))*/;
}
Rule Namespace_Lang(char* type) {
	return /*Tag(type, Strip(Prefix(type + " ", Suffix(Invalid(), ";"))))*/;
}
Rule JStructure_Lang(char* type, Rule rule) {
	/*final Rule modifiers = String*/(/*"modifiers")*/;
	/*final Rule maybeWithTypeArguments = NameWithTypeParameters*/(/*)*/;
	/*final Rule maybeWithParameters =
				Strip*/(/*Or(Suffix(First(maybeWithTypeArguments*/, /* "("*/, /* Parameters())*/, /* ")")*/, /* maybeWithTypeArguments))*/;
	/*final Rule maybeWithParameters1 =
				Or*/(/*Last(maybeWithParameters*/, /* "extends"*/, /* Node("extendsClause"*/, /* JType()))*/, /* maybeWithParameters)*/;
	/*final Rule beforeContent =
				Or*/(/*Last(maybeWithParameters1*/, /* "implements"*/, /* Node("implementsClause"*/, /* JType()))*/, /* maybeWithParameters1)*/;
	/*final Rule children = Statements*/(/*"children"*/, /* rule)*/;
	/*final Rule beforeContent1 = Or*/(/*Last(beforeContent*/, /* "permits"*/, /* Delimited("variants"*/, /* JType()*/, /* "*/, /*"))*/, /* beforeContent)*/;
	/*final Rule aClass = First*/(/*First(Strip(Or(modifiers*/, /* Empty))*/, /* type + " "*/, /* beforeContent1)*/, /* "{"*/, /* children)*/;
	return /*Tag(type, Strip(Suffix(aClass, "}")))*/;
}
Rule NameWithTypeParameters_Lang() {
	/*final Rule name = StrippedIdentifier*/(/*"name")*/;
	/*final Rule withTypeParameters = Suffix*/(/*First(name*/, /* "<", Arguments("typeParameters", Identifier())), ">")*/;
	return /*Strip(Or(withTypeParameters, name))*/;
}
Rule StructureSegment_Lang() {
	/*final LazyRule structureMember = new LazyRule*/(/*)*/;
	/*structureMember.set*/(/*Or(Structures(structureMember)*/, /*
													 Statement()*/, /*
													 JMethod()*/, /*
													 LineComment()*/, /*
													 BlockComment()*/, /*
													 Whitespace()))*/;
	return /*structureMember*/;
}
Rule BlockComment_Lang() {
	return /*Tag("block-comment", Strip(Prefix("start", Suffix(String("value"), "end"))))*/;
}
Rule LineComment_Lang() {
	return /*Tag("line-comment", Strip(Prefix("//", String("value"))))*/;
}
Rule Statement_Lang() {
	return /*Tag("statement", Strip(Suffix(Node("value", JDefinition()), ";")))*/;
}
Rule JMethod_Lang() {
	/*Rule params = Parameters*/(/*)*/;
	/*final Rule header = Strip*/(/*Suffix(Last(Node("definition"*/, /* JDefinition())*/, /* "("*/, /* params)*/, /* ")"))*/;
	/*final Rule withBody = Suffix*/(/*First(header*/, /* "{"*/, /* Statements("body"*/, /* JMethodSegment()))*/, /* "}")*/;
	return /*Tag("method", Strip(Or(Suffix(header, ";"), withBody)))*/;
}
Rule JMethodSegment_Lang() {
	/*final LazyRule rule = new LazyRule*/(/*)*/;
	/*rule.set*/(/*Strip(JMethodSegmentValue(rule)))*/;
	return /*rule*/;
}
Rule JMethodSegmentValue_Lang(LazyRule rule) {
	/*final Rule expression = JExpression*/(/*)*/;
	return /*Or(Whitespace(),
							LineComment(),
							Conditional("if", expression, rule),
							Conditional("while", expression, rule),
							Else(rule),
							Strip(Suffix(JMethodStatementValue(), ";")),
							Block(rule))*/;
}
Rule Block_Lang(LazyRule rule) {
	return /*Tag("block", Strip(Prefix("{", Suffix(Statements("children", rule), "}"))))*/;
}
Rule JMethodStatementValue_Lang() {
	/*final Rule expression = JExpression*/(/*)*/;
	return /*Or(Break(),
							Return(expression),
							Invokable(expression),
							Initialization(JDefinition(), expression),
							PostFix(expression))*/;
}
Rule Break_Lang() {
	return /*Tag("break", Strip(Prefix("break", Empty)))*/;
}
Rule PostFix_Lang(Rule expression) {
	return /*Tag("postFix", Strip(Suffix(Node("value", expression), "++")))*/;
}
Rule Initialization_Lang(Rule definition, Rule value) {
	/*final Rule definition1 = Node*/(/*"definition"*/, /* definition)*/;
	/*final Rule value1 = Node*/(/*"value"*/, /* value)*/;
	return /*First(Or(Tag("initialization", definition1), Tag("assignment", Node("location", value))), "=", value1)*/;
}
Rule Invokable_Lang(Rule expression) {
	return /*Tag("invokable", First(Node("caller", expression), "(", Arguments("arguments", expression)))*/;
}
Rule Return_Lang(Rule expression) {
	return /*Tag("return", Prefix("return ", Node("value", expression)))*/;
}
Rule Else_Lang(Rule statement) {
	return /*Tag("else", Prefix("else ", Node("child", statement)))*/;
}
Rule Conditional_Lang(char* tag, Rule expression, Rule statement) {
	/*final Rule condition = Node*/(/*"condition"*/, /* expression)*/;
	/*final Rule body = Node*/(/*"body"*/, /* statement)*/;
	/*final Rule split = Split*/(/*Prefix("("*/, /* condition)*/, /*
														 KeepFirst(new FoldingDivider(new EscapingFolder(new ClosingParenthesesFolder())))*/, /*
														 body)*/;
	return /*Tag(tag, Prefix(tag + " ", Strip(split)))*/;
}
Rule JExpression_Lang() {
	return /*Invalid()*/;
}
Rule CExpression_Lang() {
	return /*Or(Invalid())*/;
}
Rule CFunctionSegment_Lang() {
	/*final LazyRule rule = new LazyRule*/(/*)*/;
	/*rule.set*/(/*Or(Whitespace()*/, /* Prefix(System.lineSeparator() + "\t"*/, /* CFunctionSegmentValue(rule))))*/;
	return /*rule*/;
}
Rule CFunctionSegmentValue_Lang(LazyRule rule) {
	return /*Or(LineComment(),
							Conditional("if", CExpression(), rule),
							Conditional("while", CExpression(), rule),
							Break(),
							Else(rule),
							CFunctionStatement(),
							Block(rule),
							Invalid())*/;
}
Rule CFunctionStatement_Lang() {
	return /*Or(Suffix(CFunctionStatementValue(), ";"))*/;
}
Rule CFunctionStatementValue_Lang() {
	/*final Rule expression = CExpression*/(/*)*/;
	return /*Or(Return(JExpression()),
							Invokable(expression),
							Initialization(CDefinition(), expression),
							PostFix(expression))*/;
}
Rule Parameters_Lang() {
	return /*Arguments("params", Or(ParameterDefinition(), Whitespace()))*/;
}
Rule ParameterDefinition_Lang() {
	// Use TypeFolder to properly parse generic types like Function<T, R>
	// Parameters don't have modifiers, just type and name
	/*final FoldingDivider typeDivider = new FoldingDivider*/(/*new TypeFolder())*/;
	/*final Splitter typeSplitter = KeepLast*/(/*typeDivider)*/;
	return /*Tag("definition", new SplitRule(Node("type", JType()), String("name"), typeSplitter))*/;
}
Rule JDefinition_Lang() {
	// Use TypeFolder to properly parse generic types like Function<T, R>
	// Split into modifiers+type and name using type-aware splitting
	/*final Rule type = Node*/(/*"type"*/, /* JType())*/;
	/*final Rule name = String*/(/*"name")*/;
	// Handle optional modifiers before type
	/*final Rule modifiers = Delimited*/(/*"modifiers"*/, /* Tag("modifier"*/, /* String("value"))*/, /* " ")*/;
	/*final Rule withModifiers = Split*/(/*modifiers*/, /* KeepLast(new FoldingDivider(new TypeFolder()))*/, /* type)*/;
	/*Rule beforeName = Or*/(/*withModifiers*/, /* type)*/;
	return /*Tag("definition", Strip(Last(beforeName, " ", name)))*/;
}
Rule JType_Lang() {
	/*final LazyRule type = new LazyRule*/(/*)*/;
	/*type.set*/(/*Or(Generic(type)*/, /* Array(type)*/, /* Identifier()*/, /* Invalid()))*/;
	return /*type*/;
}
Rule Array_Lang(Rule type) {
	return /*Tag("array", Strip(Suffix(Node("child", type), "[]")))*/;
}
Rule Identifier_Lang() {
	return /*Tag("identifier", StrippedIdentifier("value"))*/;
}
Rule StrippedIdentifier_Lang(char* key) {
	return /*Strip(FilterRule.Identifier(String(key)))*/;
}
Rule Generic_Lang(Rule type) {
	return /*Tag("generic",
							 Strip(Suffix(First(Strip(String("base")), "<", NodeListRule.Arguments("arguments", type)), ">")))*/;
}
Rule Invalid_Lang() {
	return /*Tag("invalid", Placeholder(String("value")))*/;
}
