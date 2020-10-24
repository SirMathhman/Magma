//
// Created by mathm on 10/23/2020.
//

#ifndef MAGMA_EXCEPTION_H
#define MAGMA_EXCEPTION_H

#include "Core.h"
#include "Option.h"

void throw(Any *value);

Option catch();

Bool catch_();

#include "../../app.h"

#endif //MAGMA_EXCEPTION_H
