#include <stdio.h>

struct Calculator {
};

struct Calculator Calculator() {
    struct Calculator this;
    return this;
}

int32_t main() {
    printf("%s", "Hello World!");
    return 0;
}