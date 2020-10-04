//
// Created by mathm on 10/4/2020.
//

#ifndef MAGMA_MAIN_H
#define MAGMA_MAIN_H

typedef unsigned char U8;
typedef unsigned int U16;
typedef unsigned long U32;
typedef unsigned long long U64;

typedef char I8;
typedef int I16;
typedef long I32;
typedef long long I64;
typedef void Void;

typedef Void Any;
typedef I16 Bool;
typedef I8 *String;

typedef struct Exception {
    String message;
    struct Exception *trace;
} Exception;

void throw(Exception* exception);

void catch(void (*action)(Exception*));

#endif //MAGMA_MAIN_H
