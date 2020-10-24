//
// Created by mathm on 10/23/2020.
//

#ifndef MAGMA_CORE_H
#define MAGMA_CORE_H

typedef void Any;
typedef int Bool;

#define false 0
#define true 1
#define null 0

typedef struct Function {
    Any *caller;
    Any *value;
} Function;

Function Function_(Any *caller, Any *value);

Function Global_(Any *value);

#endif //MAGMA_CORE_H
