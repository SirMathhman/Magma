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
struct IOErrorVTable {};
struct IOError {
	IOErrorVTable vtable;
};
struct PathVTable {};
struct Path {
	PathVTable vtable;
};
struct IO {
};
#endif