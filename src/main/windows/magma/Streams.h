#ifndef STREAMS_H
#define STREAMS_H
#include "../magma/Collectors.h"
#include "../magma/Heads.h"
#include "../magma/Main.h"
#include "../magma/Options.h"
template <typeparam T>
struct Stream {
	Head<T> head;
};
struct Streams {
};
#endif