#ifndef magma_collect_list_ListCollector
#define magma_collect_list_ListCollector
#include "../../../windows/collect/list/Lists.h"
#include "../../../magma/collect/stream/Collector.h"
struct ListCollector<T>{
};
// expand magma.collect.list.List_<T>
// expand magma.collect.list.List_<T>
// expand magma.collect.list.List_<T>
magma.collect.list.List_<T> createInitial();
magma.collect.list.List_<T> fold(magma.collect.list.List_<T> tList, T t);
#endif

