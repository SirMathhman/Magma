#include <stdlib.h>
struct Outer {
};
struct Inner {
};
struct List {
};
struct List List() {
    static int init;
    static struct List this;
    if (!init) {
        init = 1;
    }
    return this;
}
struct Inner Inner() {
    static int init;
    static struct Inner this;
    if (!init) {
        init = 1;
    }
    return this;
}
struct Outer Outer() {
    static int init;
    static struct Outer this;
    if (!init) {
        init = 1;
    }
    return this;
}
