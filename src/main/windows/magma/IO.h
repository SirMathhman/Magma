// File generated from '.\src\main\java\magma\IO.java'. This is not source code!
#ifndef IO_H
#define IO_H
#include "../magma/Collections.h"
#include "../magma/Options.h"
#include "../magma/Results.h"
#include "../magma/Streams.h"
struct IOError;
struct Path;
struct IO;
struct IOErrorVTable {
	char* (*display)(void*);
};
struct IOError {
	void* data;
	IOErrorVTable vtable;
};
struct PathVTable {
	boolean (*exists)(void*);
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
	void* data;
	PathVTable vtable;
};
struct IO {
};
#endif