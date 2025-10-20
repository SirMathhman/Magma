#ifndef COLLECTORS_H
#define COLLECTORS_H
#include "../magma/Collections.h"
#include "../java/util/function.h"
template <typeparam T, typeparam C>
struct Collector {
};
template <typeparam T>
struct ListCollector {
};
struct Joiner {
	char* delimiter;
};
template <typeparam T>
struct AnyMatch {
	Predicate<T> predicate;
};
struct Collectors {
};
#endif