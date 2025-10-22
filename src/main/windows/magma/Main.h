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
struct Struct;
struct EnumNode;
struct Union;
struct Main;
enum JMethodHeaderTag {
	JConstructorType,
	DefinableType
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
	CIdentifierType,
	ContentType
};
union CExpressionData {
	CIdentifier cidentifier;
	Content content;
};
struct CExpression {
	CExpressionTag tag;
	CExpressionData data;
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
	DefinableTag tag;
	DefinableData data;
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
struct Struct {
	ArrayList<char*> typeParameters;
	char* name;
	Option<char*> maybeFields;
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
struct Placeholder {
	char* input;
};
char* generate_Definable(void* _ref);
char* generate_CExpression(void* _ref);
char* generate_CRootSegment(void* _ref);
#endif