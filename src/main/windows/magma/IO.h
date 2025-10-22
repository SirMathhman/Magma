// File generated from '.\src\main\java\magma\IO.java'. This is not source code!
#ifndef IO_H
#define IO_H
#include "../magma/Collections.h"
#include "../magma/Options.h"
#include "../magma/Results.h"
#include "../magma/Streams.h"
#include <stdbool.h>
struct IOError;
struct Path;
struct IO;
struct PathVTable {
	bool (*exists)(void*);
	Result<char*, IOError> (*readString)(void*);
	Option<IOError> (*createDirectories)(void*);
	Option<IOError> (*writeString)(void*, char*);
	Path (*getParent)(void*);
	Result<ArrayList<Path>, IOError> (*walk)(void*);
	char* (*asString)(void*);
	Path (*relativize)(void*, Path);
	Path (*resolveByPath)(void*, Path);
	Stream<char*> (*stream)(void*);
	Path (*getFileName)(void*);
	Path (*resolveByString)(void*, char*);
};
struct Path {
};
struct IO {
};
struct IOErrorVTable {
	char* (*display)(void*);
};
struct IOError {
};
char* display_IOError(void* _ref);
bool exists_Path(void* _ref);
Result<char*, IOError> readString_Path(void* _ref);
Option<IOError> createDirectories_Path(void* _ref);
Option<IOError> writeString_Path(void* _ref, char* output);
Path getParent_Path(void* _ref);
Result<ArrayList<Path>, IOError> walk_Path(void* _ref);
char* asString_Path(void* _ref);
Path relativize_Path(void* _ref, Path path);
Path resolveByPath_Path(void* _ref, Path path);
Stream<char*> stream_Path(void* _ref);
Path getFileName_Path(void* _ref);
Path resolveByString_Path(void* _ref, char* name);
#endif