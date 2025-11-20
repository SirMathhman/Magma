#include <stddef.h>
#include <stdint.h>

int main(void) {
    int array = malloc((sizeof(int32_t) * 100));
    for (int i = 0; i < 100; i++) {
        array[i] = i;
    }
    printf("%d", array[2]);
}
