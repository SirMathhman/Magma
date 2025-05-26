import { Head } from "../../../../magma/api/collect/head/Head";
import { Iter } from "../../../../magma/api/collect/Iter";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export class FlatMapHead<T, R> implements Head<R> {
	mapper: (arg0 : T) => Iter<R>;
	head: Head<T>;
	current: Iter<R>;
	constructor (head: Head<T>, initial: Iter<R>, mapper: (arg0 : T) => Iter<R>) {
		this.head/*unknown*/ = head/*Head<T>*/;
		this.current/*unknown*/ = initial/*Iter<R>*/;
		this.mapper/*unknown*/ = mapper/*(arg0 : T) => Iter<R>*/;
	}
	next(): Option<R> {
		while (true/*unknown*/){
			let next = this.current.next()/*unknown*/;
			if (next.isPresent()/*unknown*/){
				return next/*unknown*/;
			}
			let tuple = this.head.next().map(this.mapper).toTuple(this.current)/*unknown*/;
			if (tuple.left()/*unknown*/){
				this.current/*unknown*/ = tuple.right()/*unknown*/;
			}
			else {
				return new None<R>()/*unknown*/;
			}
		}
	}
}
