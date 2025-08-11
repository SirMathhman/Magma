#include <stdio.h>

struct Calculator {
};

int32_t add_Calculator(int32_t first, int32_t second, struct Calculator* this) {
    return this->first + this->second;
}

int32_t multiply_Calculator(int32_t first, int32_t second, struct Calculator* this) {
    return this->first * this->second;
}

struct Calculator Calculator() {
    struct Calculator this;
    return this;
}

int32_t main(){struct Calculator myCalculator = Calculator();

int32_t once = myCalculator.add(1, 2);

int32_t twice = myCalculator.multiply(3, 4); printf("%s", "Hello World!"); return 0;}