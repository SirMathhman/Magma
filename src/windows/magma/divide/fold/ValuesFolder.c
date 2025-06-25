#include "ValuesFolder.h"
#include "magma/divide/State.h"
/*public */struct ValuesFolder {/*
*/};
/*@Override
    public */struct State fold_ValuesFolder(/*final */struct State state, /*final */struct char c) {
	/*if (',' */ = /*= c)
            return state*/.advance();
	return state.append(c);
}
/*
*/