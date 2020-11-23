//
// Created by mathm on 11/23/2020.
//

#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <stdio.h>
#include "Main.h"
#include "Core.h"

Bool isPresent_Some(Option *this) {
    return true;
}

Any *get_Some(Option *this) {
    return this->value;
}

Option Some(Any *value) {
    Option this;
    this.isPresent = isPresent_Some;
    this.get = get_Some;
    this.value = value;
    return this;
}

Bool isPresent_None(Option *this) {
    return false;
}

Any *get_None(Option *this) {
    return 0;
}

Option None() {
    Option this;
    this.isPresent = isPresent_None;
    this.get = get_None;
    return this;
}