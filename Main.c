//
// Created by mathm on 11/23/2020.
//

#include "Main.h"
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

int main() {
    FILE *file = fopen("Main.mg", "r");
    if(file) {
        fclose(file);
    } else {
        printf("Failed to open Main.mg: %s", strerror(errno));
    }
    return 0;
}
