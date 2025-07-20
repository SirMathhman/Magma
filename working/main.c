#include <stdlib.h>
struct main_t {
    int myValue;
};
void inner_main(struct main_t this) {
}
int main() {
    struct main_t this;
    this.myValue = 100;
    inner_main(this);
    return 0;
}
