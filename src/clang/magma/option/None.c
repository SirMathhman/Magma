#include "None.h"
struct Option_R map(struct R(*mapper)(struct T)){return new None<>();
}
struct T orElseGet(struct Supplier_T other){return other.get();
}
struct Tuple_Boolean_T toTuple(struct T other){return new Tuple<>(false, other);
}
struct void ifPresent(struct Consumer_T consumer){
}
struct T orElse(struct T other){return other;
}
struct Option_T filter(struct Predicate_T predicate){return new None<>();
}
int isPresent(){return false;
}
struct R match(struct R(*ifPresent)(struct T), struct Supplier_R ifEmpty){return ifEmpty.get();
}
int isEmpty(){return true;
}
struct Option_T or(struct Supplier_Option_T other){return other.get();
}
struct Option_R flatMap(struct Option_R(*mapper)(struct T)){return new None<>();
}

