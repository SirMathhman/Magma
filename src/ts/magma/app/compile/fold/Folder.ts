import { DivideState } from "../../../../magma/app/compile/DivideState";
export interface Folder {
	apply(divideState: DivideState, c: string): DivideState;
}
