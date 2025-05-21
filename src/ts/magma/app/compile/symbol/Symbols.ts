import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Type } from "../../../../magma/app/compile/type/Type";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
import { Strings } from "../../../../magma/api/text/Strings";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
import { Some } from "../../../../magma/api/option/Some";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
import { Symbol } from "../../../../magma/app/compile/value/Symbol";
import { None } from "../../../../magma/api/option/None";
import { HeadedIter } from "../../../../magma/api/collect/head/HeadedIter";
import { RangeHead } from "../../../../magma/api/collect/head/RangeHead";
import { Characters } from "../../../../magma/api/text/Characters";
import { Value } from "../../../../magma/app/compile/value/Value";
export class Symbols {
	static parseSymbolType(state: CompileState, input: string): Option<Tuple2<CompileState, Type>> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (Symbols.isSymbol(stripped)/*unknown*/){
			let resolved = TypeCompiler.addResolvedImportFromCache0(state, stripped)/*unknown*/;
			return new Some<Tuple2<CompileState, Type>>(new Tuple2Impl<CompileState, Type>(resolved, new Symbol(stripped)))/*unknown*/;
		}
		return new None<Tuple2<CompileState, Type>>()/*unknown*/;
	}
	static isSymbol(input: string): boolean {
		let query = new HeadedIter<number>(new RangeHead(Strings.length(input)))/*unknown*/;
		return query.allMatch((index: number) => Symbols.isSymbolChar(index, input.charAt(index))/*unknown*/)/*unknown*/;
	}
	static isSymbolChar(index: number, c: string): boolean {
		return "_" === c || Characters.isLetter(c) || (0 !== index && Characters.isDigit(c))/*unknown*/;
	}
	static parseSymbolValue(state: CompileState, input: string): Option<Tuple2<CompileState, Value>> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (Symbols.isSymbol(stripped)/*unknown*/){
			let withImport = TypeCompiler.addResolvedImportFromCache0(state, stripped)/*unknown*/;
			return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(withImport, new Symbol(stripped)))/*unknown*/;
		}
		else {
			return new None<Tuple2<CompileState, Value>>()/*unknown*/;
		}
	}
}
