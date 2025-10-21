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
struct Definable;
struct JMethodHeader;
struct CExpression;
struct ParseState;
struct DivideState;
struct Definition;
struct Placeholder;
struct JConstructor;
struct Content;
struct CIdentifier;
struct Location;
struct Main;
enum DefinableTag {
	DefinitionTag,
	PlaceholderTag
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
	JConstructorTag,
	DefinableTag
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
	CIdentifierTag,
	ContentTag
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
};
struct DivideState {
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
char* generate_Definable(void* _ref);
char* generate_CExpression(void* _ref);
ArrayList<> new_ArrayList<>(void* _ref);
#endif