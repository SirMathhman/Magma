package magma.transform;

import magma.compile.Lang;

import java.util.Collections;
import java.util.List;

public class RootSegmentTransformer {
	static List<Lang.CRootSegment> flattenRootSegment(Lang.JavaRootSegment segment) {
		return switch (segment) {
			case Lang.JStructure jStructure -> StructureTransformer.flattenStructure(jStructure);
			case Lang.Invalid invalid -> List.of(invalid);
			default -> Collections.emptyList();
		};
	}
}
