import { Iter } from "../../../../magma/api/collect/Iter";
interface Divider {
	divide(input: string): Iter<string>;
}
