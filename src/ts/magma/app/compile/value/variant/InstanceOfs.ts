import { Value } from "../../../../../magma/app/compile/value/Value";
import { Type } from "../../../../../magma/app/compile/type/Type";
import { Rule } from "../../../../../magma/app/compile/rule/Rule";
import { Generator } from "../../../../../magma/app/compile/Generator";
export class InstanceOf implements Value {
	value: Value;
	type: Type;
	constructor (value: Value, type: Type) {
		this.value = value;
		this.type = type;
	}
}
export class InstanceOfs {
	static createRule(): Rule<Value> {
		/*return new ComposableRule((CompileState state) -> Split.first(" instanceof ", (String valueString, String typeString) -> ValueCompiler.createValueRule().apply(state, valueString).flatMap(valueTuple -> {
            return TypeCompiler.createTypeRule().apply(valueTuple.left(), typeString).map((Tuple2<CompileState, Type> typeTuple) -> {
                return new Tuple2Impl<CompileState, Value>(typeTuple.left(), new InstanceOf(valueTuple.right(), typeTuple.right()));
            });
        })))*/;
	}
	static generate(instanceOf: InstanceOf, valueGenerator: Generator<Value>, typeGenerator: Generator<Type>): string {
		return valueGenerator.generate(instanceOf.value) + "._variant === " + typeGenerator.generate(instanceOf.type) + "._variantKey"/*unknown*/;
	}
}
