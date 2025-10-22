// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
#ifndef MAIN_H
#define MAIN_H
#include "../magma/Collections.h"
#include "../magma/Collectors.h"
#include "../magma/Functions.h"
#include "../magma/IO.h"
#include "../magma/JavaImpl.h"
#include "../magma/Options.h"
#include "../magma/Results.h"
#include "../magma/Streams.h"
#include "../magma/Utils.h"
#include <stdbool.h>
struct Definable;
struct JMethodHeader;
struct CExpression;
struct CRootSegment;
struct ParseState;
struct DivideState;
struct Definition;
struct Placeholder;
struct JConstructor;
struct Content;
struct CIdentifier;
struct Location;
struct CStruct;
struct EnumNode;
struct Union;
struct JStructure;
struct Main;
struct Main {
};
enum JMethodHeaderTag {
	JConstructorType,
	DefinableType
};
union JMethodHeaderData {
	JConstructor jconstructor;
	Definable definable;
};
struct JMethodHeader {
};
struct ParseState {
	Stack<ArrayList<char*>> beforeStatements;
	Map<char*, ArrayList<char*>> structDependencies;
	Map<char*, ArrayList<CRootSegment>> rootSegments;
	ArrayList<char*> beforeStructs;
	Option<char*> maybeCurrentStructName;
	ArrayList<char*> structFields;
	ArrayList<char*> afterStatements;
	ArrayList<char*> functions;
	int counter;
	ArrayList<char*> includes;
	ArrayList<char*> functionDeclarations;
	bool usesBoolean;
	ArrayList<char*> typeUsages;
};
enum CExpressionTag {
	CIdentifierType,
	ContentType
};
union CExpressionData {
	CIdentifier cidentifier;
	Content content;
};
struct CExpression {
};
enum DefinableTag {
	DefinitionType,
	PlaceholderType
};
union DefinableData {
	Definition definition;
	Placeholder placeholder;
};
struct Definable {
};
enum CRootSegmentTag {
	EnumNodeType,
	CStructType,
	UnionType
};
union CRootSegmentData {
	EnumNode enumnode;
	CStruct cstruct;
	Union union;
};
struct CRootSegment {
};
struct JStructure {
	ArrayList<char*> annotations;
	char* type;
	char* name;
	ArrayList<char*> typeParameters;
	ArrayList<char*> variants;
	StringBuilder fields;
};
struct Location {
	ArrayList<char*> namespace;
	char* name;
};
struct CIdentifier {
	char* value;
};
struct Content {
	char* value;
};
struct Union {
	ArrayList<char*> typeParameters;
	char* name;
	char* fields;
};
struct DivideState {
	char* input;
	ArrayList<char*> segments;
	StringBuilder buffer;
	int depth;
	int index;
};
struct EnumNode {
	char* name;
	ArrayList<char*> variants;
};
struct Definition {
	ArrayList<char*> annotations;
	char* type;
	char* name;
};
struct JConstructor {
	char* name;
};
struct CStruct {
	ArrayList<char*> typeParameters;
	char* name;
	Option<char*> maybeFields;
};
struct Placeholder {
	char* input;
};
char* generate_Definable(void* _ref);
char* generate_CExpression(void* _ref);
char* generate_CRootSegment(void* _ref);
#endif