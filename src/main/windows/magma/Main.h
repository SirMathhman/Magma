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
template <typename A, typename B>
struct Tuple;
struct Definition;
struct Placeholder;
struct JConstructor;
struct Content;
struct CIdentifier;
struct Location;
struct methods */";
		};

		final String outputParamsString = ";
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
	Stack<ArrayList<char*>> beforeStatements;
	ArrayList<char*> beforeStructs;
	ArrayList<char*> structFields;
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
struct methods */";
		};

		final String outputParamsString = "VTable {
	char* (*generate)(void*);
	char* (*generate)(void*);
	/* Constructors not allowed as interface methods */(void*);
	/*=*/ (*compileMethodStatements)(void*);
	/* Constructors not allowed as interface methods */(void*);
	/* Constructors not allowed as interface methods */(void*);
	/* Constructors not allowed as interface methods */(void*);
};
struct methods */";
		};

		final String outputParamsString = " {/*" + joinedOutputParams + ")";
		final String outputMethodHeader = transformMethodHeader(methodHeader, structName).generate() + outputParamsString;

		if (withBraces.equals(";") || isPlatformDependentMethod(methodHeader)*/
	void* data;
	methods */";
		};

		final String outputParamsString = "VTable vtable;
	/*final String joinedTypes = outputParams.stream().map(Definition::type).collect(new Joiner(", "));*/
	/*final String functionDeclaration = generateStatement(field + "(" + joinedTypes + ")", 1);*/
	/*final ArrayList<String> paramNames = params.stream().map(Definition::name).collect(new ListCollector<>());*/
	/*final ArrayList<String> stringArrayList =
					paramNames.subList(1, paramNames.size()).orElse(new ArrayList<>()).addFirst("this.data");*/
	/*final String joinedArgs = stringArrayList.stream().collect(new Joiner(", "));*/
	/*final ParseState withFunctionDeclaration = state
					.addStructField(functionDeclaration)
					.addFunctionDeclaration(outputMethodHeader + ";" + System.lineSeparator())
					.addFunction(
							outputMethodHeader + "{" + generateStatement(structName + " this = *((" + structName + "*) _ref)", 1) +
							generateStatement("return this.vtable.apply(" + joinedArgs + ")", 1) + System.lineSeparator() + "}" +
							System.lineSeparator());*/
	/*return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("", withFunctionDeclaration));*/
	/*} else if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
			final String inputBody = withBraces.substring(1, withBraces.length() - 1);*/
	/*ArrayList<String> statements = compiledBody.left;*/
	/*if (Objects.requireNonNull(methodHeader) instanceof JConstructor) {
				ArrayList<String> stringArrayList = statements.addFirst(generateStatement(structName + " this", 1));
				statements = stringArrayList.addLast(generateStatement("return this", 1));
			}*/
	/*final String joined = statements.stream().collect(new Joiner(""));*/
	/*final String outputBodyWithBraces = "{" + joined + System.lineSeparator() + "}";*/
	/*return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("", parseState));*/
	/*}*/
};
struct Main {
};
char* generate_Definable(void* _ref);
char* generate_CExpression(void* _ref);
ArrayList<> new_ArrayList<>(void* _ref);
/*=*/ compileMethodStatements_methods */";
		};

		final String outputParamsString = "(void* _ref);
System.lineSeparator new_System.lineSeparator(void* _ref);
state.addFunction new_state.addFunction(void* _ref);
ParseState>> new_ParseState>>(void* _ref);
#endif