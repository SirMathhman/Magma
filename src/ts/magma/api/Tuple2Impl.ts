import { Tuple2 } from "../../magma/api/Tuple2";
export class Tuple2Impl<A, B> implements Tuple2<A, B> {
	leftNode: A;
	rightNode: B;
	constructor (leftNode: A, rightNode: B) {
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}
	left(): A {
		return this.leftNode/*unknown*/;
	}
	right(): B {
		return this.rightNode/*unknown*/;
	}
}
