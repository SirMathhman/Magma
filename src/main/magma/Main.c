#include <stdio.h>

struct Calculator {
};

int32_t add_Calculator(struct Calculator* this, int32_t first, int32_t second) {
    return first + second;
}

int32_t multiply_Calculator(struct Calculator* this, int32_t first, int32_t second) {
    return first * second;
}

struct Calculator Calculator() {
    struct Calculator this;
    return this;
}

int32_t main(){struct Calculator myCalculator = Calculator();

int32_t once = add_Calculator(&myCalculator, 1, 2);

int32_t twice = multiply_Calculator(&myCalculator, 3, 4); printf("%s", "Hello World!"); return 0;}