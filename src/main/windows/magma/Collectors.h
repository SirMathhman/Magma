// File generated from '.\src\main\java\magma\Collectors.java'. This is not source code!
#ifndef COLLECTORS_H
#define COLLECTORS_H
#include "../magma/Collections.h"
#include "../magma/Results.h"
template <typename T, typename C>
struct Collector {
};
template <typename T>
struct ListCollector {
};
struct Joiner {
	char* delimiter;
};
template <typename T>
struct AnyMatch {
	Predicate<T> predicate;
};
template <typename T, typename X, typename C>
struct ResultCollector {
	Collector<T, C> collector;
};
template <typename T>
struct AllMatch {
	Predicate<T> predicate;
};
struct Collectors {
};
#endif