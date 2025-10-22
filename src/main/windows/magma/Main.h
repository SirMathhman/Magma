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
struct ParseState {
	Stack<ArrayList<char*>> beforeStatements;
	ArrayList<char*> beforeStructs;
	ListMap<char*, ArrayList<char*>> structDependencies;
	ListMap<char*, ArrayList<CRootSegment>> rootSegments;
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
struct JStructure {
	ArrayList<char*> annotations;
	char* type;
	char* name;
	ArrayList<char*> typeParameters;
	ArrayList<char*> variants;
	char* fields;
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
struct Union {
	ArrayList<char*> typeParameters;
	char* name;
	char* fields;
};
struct EnumNode {
	char* name;
	ArrayList<char*> variants;
};
struct CStruct {
	ArrayList<char*> typeParameters;
	char* name;
	Option<char*> maybeFields;
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
struct JConstructor {
	char* name;
};
struct Placeholder {
	char* input;
};
struct Definition {
	ArrayList<char*> annotations;
	char* type;
	char* name;
};
struct DivideState {
	char* input;
	ArrayList<char*> segments;
	char* buffer;
	int depth;
	int index;
};
char* generate_Definable(void* _ref);
char* generate_CExpression(void* _ref);
char* generate_CRootSegment(void* _ref);
#endif