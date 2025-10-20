#ifndef COLLECTORS_H
#define COLLECTORS_H
#include "../magma/Collections.h"
#include "../magma/Results.h"
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
template <typeparam T, typeparam X, typeparam C>
struct ResultCollector {
	Collector<T, C> collector;
};
template <typeparam T>
struct AllMatch {
	Predicate<T> predicate;
};
struct Collectors {
};
#endif