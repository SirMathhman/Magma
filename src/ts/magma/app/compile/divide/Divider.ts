import { Iter } from "../../../../magma/api/collect/Iter";
export interface Divider {
	divide(input: string): Iter<string>;
}
