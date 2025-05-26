import { List } from "../../../../magma/api/collect/list/List";
export interface ListsInstance {
	empty<T>(): List<T>;

	of<T>(?elements: ?): List<T>;

}
export declare const Lists: ListsInstance;
