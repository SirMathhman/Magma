import { Splitter } from "../../../../magma/app/compile/split/Splitter";
import { Folder } from "../../../../magma/app/compile/fold/Folder";
import { Selector } from "../../../../magma/app/compile/select/Selector";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
import { FoldedDivider } from "../../../../magma/app/compile/divide/FoldedDivider";
import { DecoratedFolder } from "../../../../magma/app/compile/fold/DecoratedFolder";
import { ListCollector } from "../../../../magma/api/collect/list/ListCollector";
import { None } from "../../../../magma/api/option/None";
export class FoldingSplitter implements Splitter {
	folder: Folder;
	selector: Selector;
	constructor (folder: Folder, selector: Selector) {
		this.folder = folder;
		this.selector = selector;
	}
	apply(input: string): Option<Tuple2<string, string>> {
		let divisions = new FoldedDivider(new DecoratedFolder(this.folder)).divide(input).collect(new ListCollector<string>())/*unknown*/;
		if (2 > divisions.size()/*unknown*/){
			return new None<Tuple2<string, string>>()/*unknown*/;
		}
		return this.selector.select(divisions)/*unknown*/;
	}
}
