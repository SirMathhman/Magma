// File generated from '.\src\main\java\magma\Main.java'. This is not source code!
#ifndef MAIN_H
#define MAIN_H
#include "../magma/Collections.h"
#include "../magma/Collectors.h"
#include "../magma/IO.h"
#include "../magma/JavaImpl.h"
#include "../magma/Options.h"
#include "../magma/Results.h"
#include "../magma/Streams.h"
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
struct ParseState {
	Stack<ArrayList<char*>> beforeStatements;
	ArrayList<char*> afterStatements;
	ArrayList<char*> structs;
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
template <typename A, typename B>
struct Tuple {
	A left;
	B right;
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
struct Main {
};
#endif