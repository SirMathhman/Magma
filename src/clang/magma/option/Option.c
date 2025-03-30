#include "Option.h"
struct Option_R map(struct R(*mapper)(struct T));
struct T orElseGet(struct T(*other)());
struct Tuple_Boolean_T toTuple(struct T other);
struct void ifPresent(struct Consumer_T consumer);
struct T orElse(struct T other);
struct Option_T filter(struct Predicate_T predicate);
int isPresent();
struct R match(struct R(*ifPresent)(struct T), struct R(*ifEmpty)());
int isEmpty();
struct Option_T or(struct Option_T(*other)());
struct Option_R flatMap(struct Option_R(*mapper)(struct T));

