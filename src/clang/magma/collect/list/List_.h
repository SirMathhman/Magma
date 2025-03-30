#ifndef magma_collect_list_List_
#define magma_collect_list_List_
#include "../../../magma/collect/stream/Stream.h"
#include "../../../magma/option/Option.h"
#include "../../../magma/option/Tuple.h"
struct List_<T>{
};
// expand Stream_T = Stream<struct T>
// expand List__T = List_<struct T>
// expand List__T = List_<struct T>
// expand List__T = List_<struct T>
// expand Option_T = Option<struct T>
// expand List__T = List_<struct T>
// expand List__T = List_<struct T>
// expand List__T = List_<struct T>
// expand BiFunction_T_T_Integer = BiFunction<struct T, struct T, struct Integer>
// expand Option_Tuple_T_List__T = Option<struct Tuple_T_List__T>
// expand Tuple_T_List__T = Tuple<struct T, struct List__T>
// expand List__T = List_<struct T>
struct Stream_T stream();
struct List__T add(struct T element);
struct List__T addAll(struct List__T others);
struct Option_T findFirst();
struct int size();
struct List__T subList(struct int start, struct int end);
int equalsTo(struct List__T other);
struct List__T sort(struct BiFunction_T_T_Integer comparator);
struct T get(struct int index);
struct Option_Tuple_T_List__T popFirst();
int isEmpty();
#endif

