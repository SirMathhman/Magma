#include "SingleHead.h"
struct public SingleHead(struct T value){this.value = value;
}
struct Option_T next(){if (retrieved) return new None<>();

        retrieved = true;
        return new Some<>(value);
}

