// File generated from '.\src\main\java\magma\Collectors.java'. This is not source code!
#ifndef COLLECTORS_H
#define COLLECTORS_H
#include "../magma/Collections.h"
#include "../magma/Results.h"
#include "../magma/Utils.h"
template <typename T, typename C>
struct Collector;
template <typename T>
struct ListCollector;
struct Joiner;
template <typename T>
struct AnyMatch;
template <typename T, typename X, typename C>
struct ResultCollector;
template <typename T>
struct AllMatch;
template <typename K, typename V>
struct MapCollector;
struct Collectors;
template <typename T, typename X, typename C>
struct ResultCollector {
	Collector<T, C> collector;
};
struct Collectors {
};
template <typename K, typename V>
struct MapCollector {
};
template <typename T>
struct AllMatch {
	Predicate<T> predicate;
};
template <typename T>
struct AnyMatch {
	Predicate<T> predicate;
};
struct Joiner {
	char* delimiter;
};
template <typename T>
struct ListCollector {
};
template <typename T, typename C>
struct CollectorVTable {
	C (*createInitial)(void*);
	C (*fold)(void*, C, T);
};
template <typename T, typename C>
struct Collector {
};
template <typename T, typename C>
C createInitial_Collector(void* _ref);
template <typename T, typename C>
C fold_Collector(void* _ref, C current, T element);
#endif