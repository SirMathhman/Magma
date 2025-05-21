import { Registry } from "../../../magma/app/compile/Registry";
import { Context } from "../../../magma/app/compile/Context";
import { Stack } from "../../../magma/app/compile/Stack";
export interface CompileState {
	createIndent(): string;
	mapRegistry(mapper: (arg0 : Registry) => Registry): CompileState;
	mapContext(mapper: (arg0 : Context) => Context): CompileState;
	enterDepth(): CompileState;
	exitDepth(): CompileState;
	mapStack(mapper: (arg0 : Stack) => Stack): CompileState;
	findContext(): Context;
	findRegistry(): Registry;
	findStack(): Stack;
}
