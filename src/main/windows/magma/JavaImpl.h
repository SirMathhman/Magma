#ifndef JAVAIMPL_H
#define JAVAIMPL_H
#include "../magma/Collections.h"
#include "../magma/Collectors.h"
#include "../magma/IO.h"
#include "../magma/Options.h"
#include "../magma/Results.h"
#include "../java/io.h"
#include "../java/nio/file.h"
#include "../java/util/stream.h"
struct JIOError {
	IOException e;
};
struct JavaPath {
	/*java.nio.file.Path*/ path;
};
struct Paths {
};
struct JavaImpl {
};
#endif