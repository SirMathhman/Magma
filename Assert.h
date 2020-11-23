//
// Created by mathm on 11/23/2020.
//

#ifndef MAGMA_ASSERT_H
#define MAGMA_ASSERT_H
#include "Core.h"

Void setUp(I8* name);

Void pass();

Void fail();

Void assertTrue(Bool value);

Void assertFalse(Bool value);

Void assertSame(Any* expected, Any* actual);

Void assertNull(Any* value);

#endif //MAGMA_ASSERT_H
