#include "JavaStringType.h"
/*public */struct JavaStringType {/*
*/};
/*@Override
    public */struct CType toCType_JavaStringType() {
	return new_Pointer(CPrimitive.Char);
}
/*
*/