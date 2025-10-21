// File generated from '.\src\main\java\magma\Streams.java'. This is not source code!
#ifndef STREAMS_H
#define STREAMS_H
#include "../magma/Collectors.h"
#include "../magma/Functions.h"
#include "../magma/Heads.h"
#include "../magma/Options.h"
#include "../magma/Utils.h"
template <typename T>
struct Stream;
struct Streams;
template <typename T>
struct Stream {
	Supplier<Option<T>> head;
};
struct Streams {
};
#endif