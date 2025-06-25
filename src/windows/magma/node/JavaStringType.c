#include "JavaStringType.h"
#include "../../java/util/Optional.h"
/*public */struct JavaStringType {/*
*/};
/*@Override
    public */struct CType toCType_JavaStringType() {
	return new_Pointer(CPrimitive.Char);
}
/*@Override
    public */struct Optional_char_ptr findBaseName_JavaStringType() {
	return empty_Optional();
}
/*
*/