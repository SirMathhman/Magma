import { List } from "../../../../magma/api/collect/list/List";
export interface ListsInstance {
	empty<T>(): List<T>;

	of<T>(...elements: T[]): List<T>;

}
export declare const Lists: ListsInstance;
