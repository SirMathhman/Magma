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
struct Struct;
struct EnumNode;
struct Union;
struct Main;
enum DefinableTag {
	Definition,
	Placeholder
};
union DefinableData {
	Definition definition;
	Placeholder placeholder;
};
struct Definable {
	DefinableTag tag;
	DefinableData data;
};
enum JMethodHeaderTag {
	JConstructor,
	Definable
};
union JMethodHeaderData {
	JConstructor jconstructor;
	Definable definable;
};
struct JMethodHeader {
	JMethodHeaderTag tag;
	JMethodHeaderData data;
};
enum CExpressionTag {
	CIdentifier,
	Content
};
union CExpressionData {
	CIdentifier cidentifier;
	Content content;
};
struct CExpression {
	CExpressionTag tag;
	CExpressionData data;
};
enum CRootSegmentTag {
	EnumNode,
	Struct,
	Union
};
union CRootSegmentData {
	EnumNode enumnode;
	Struct struct;
	Union union;
};
struct CRootSegment {
	CRootSegmentTag tag;
	CRootSegmentData data;
};
struct ParseState {
	Stack<ArrayList<char*>> beforeStatements;
	ArrayList<char*> beforeStructs;
	ArrayList<char*> structFields;
	ArrayList<char*> afterStatements;
	ArrayList<CRootSegment> rootSegments;
	ArrayList<char*> functions;
	int counter;
	ArrayList<char*> includes;
};
struct DivideState {
	char* input;
	ArrayList<char*> segments;
	StringBuilder buffer;
	int depth;
	int index;
};
struct Definition {
	ArrayList<char*> annotations;
	char* type;
	char* name;
};
struct Placeholder {
	char* input;
};
struct JConstructor {
	char* name;
};
struct Content {
	char* value;
};
struct CIdentifier {
	char* value;
};
struct Location {
	ArrayList<char*> namespace;
	char* name;
};
struct Struct {
	ArrayList<char*> typeParameters;
	char* name;
	Option<char*> maybeFields;
};
struct EnumNode {
	char* name;
	ArrayList<char*> variants;
};
struct Union {
	ArrayList<char*> typeParameters;
	char* name;
	char* fields;
};
struct Main {
};
char* generate_Definable(void* _ref);
char* generate_CExpression(void* _ref);
char* generate_CRootSegment(void* _ref);
ArrayList<String> new_ArrayList<String>(void* _ref);
#endif