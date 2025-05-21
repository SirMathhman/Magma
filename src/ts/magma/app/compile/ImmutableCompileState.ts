import { CompileState } from "../../../magma/app/compile/CompileState";
import { Context } from "../../../magma/app/compile/Context";
import { Registry } from "../../../magma/app/compile/Registry";
import { Stack } from "../../../magma/app/compile/Stack";
import { ImmutableContext } from "../../../magma/app/compile/ImmutableContext";
import { ImmutableRegistry } from "../../../magma/app/compile/ImmutableRegistry";
import { ImmutableStack } from "../../../magma/app/compile/ImmutableStack";
export class ImmutableCompileState implements CompileState {
	context: Context;
	registry: Registry;
	depth: number;
	stack: Stack;
	constructor (context: Context, registry: Registry, stack: Stack, depth: number) {
		this.context/*unknown*/ = context/*Context*/;
		this.registry/*unknown*/ = registry/*Registry*/;
		this.depth/*unknown*/ = depth/*number*/;
		this.stack/*unknown*/ = stack/*Stack*/;
	}
	static createEmpty(): CompileState {
		return new ImmutableCompileState(ImmutableContext.createEmpty(), ImmutableRegistry.createEmpty(), ImmutableStack.createEmpty(), 0)/*unknown*/;
	}
	createIndent(): string {
		return "\n" + "\t".repeat(this.depth)/*unknown*/;
	}
	mapRegistry(mapper: (arg0 : Registry) => Registry): CompileState {
		return new ImmutableCompileState(this.context, mapper(this.findRegistry()), this.stack, this.depth)/*unknown*/;
	}
	mapContext(mapper: (arg0 : Context) => Context): CompileState {
		return new ImmutableCompileState(mapper(this.context), this.registry, this.stack, this.depth)/*unknown*/;
	}
	enterDepth(): CompileState {
		return new ImmutableCompileState(this.context, this.registry, this.stack, this.depth + 1)/*unknown*/;
	}
	exitDepth(): CompileState {
		return new ImmutableCompileState(this.context, this.registry, this.stack, this.depth - 1)/*unknown*/;
	}
	mapStack(mapper: (arg0 : Stack) => Stack): CompileState {
		return new ImmutableCompileState(this.context, this.registry, mapper(this.stack), this.depth)/*unknown*/;
	}
	findContext(): Context {
		return this.context/*unknown*/;
	}
	findRegistry(): Registry {
		return this.registry/*unknown*/;
	}
	findStack(): Stack {
		return this.stack/*unknown*/;
	}
}
