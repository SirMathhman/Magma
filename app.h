//
// Created by SirMathhman on 10/17/2020.
//

#ifndef MAGMA_APP_H
#define MAGMA_APP_H

typedef void Any;
typedef int Bool;

#define false 0
#define true 1
#define null 0

int run(int argc, char **argv);

typedef struct Option {
    Any *super;

    Any *(*orElse)(struct Option *this, Any *other);

    Bool (*isPresent)(struct Option *this);

    Bool (*isEmpty)(struct Option *this);

    Any *(*get)(struct Option *this);
} Option;

Option Option_(Any *super,
               Any *(*orElse)(struct Option *this, Any *other),
               Bool (*isPresent)(struct Option *this),
               Bool (*isEmpty)(struct Option *this),
               Any *(*get)(struct Option *this));

typedef struct Some {
    Any *value;

    Option (*Option)(struct Some *this);
} Some;

Some Some_(Any *value);

typedef struct None {
    Option (*Option)(struct None *this);
} None;

Option None_Option(None *this);

None None_();

void throw(Any *value);

Option catch();

typedef struct Function {
    Any *caller;
    Any *value;
} Function;

Function Global_(Any *value);

Function Function_(Any *caller, Any *value);

typedef struct CharArray {
    char *array;

    char (*get)(struct CharArray *this, int index);

    char (*set)(struct CharArray *this, int index, char value);

    int length;
} CharArray;

CharArray CharArray_(char *array, int length);

typedef struct String {
    CharArray array;

    int (*length)(struct String *);
} String;

String String_fromArray(CharArray array);

String String_(char *value);

#endif //MAGMA_APP_H
