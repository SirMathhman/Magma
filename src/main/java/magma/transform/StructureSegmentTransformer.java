package magma.transform;

import magma.Tuple;
import magma.compile.Lang;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.Collections;
import java.util.List;

public class StructureSegmentTransformer {
	static Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>> flattenStructureSegment(Lang.JStructureSegment self,
																																													String name) {
		return switch (self) {
			case Lang.Invalid invalid -> new Tuple<>(List.of(invalid), new None<>());
			case Lang.Method method -> new Tuple<>(List.of(Transformer.transformMethod(method, name)), new None<>());
			case Lang.JStructure jClass -> new Tuple<>(StructureTransformer.flattenStructure(jClass), new None<>());
			case Lang.Field field ->
					new Tuple<>(Collections.emptyList(), new Some<>(Transformer.transformDefinition(field.value())));
			case Lang.JInitialization jInitialization -> new Tuple<>(Collections.emptyList(),
																															 new Some<>(Transformer.transformDefinition(
																																	 jInitialization.definition())));
			case Lang.JDefinition jDefinition ->
					new Tuple<>(Collections.emptyList(), new Some<>(Transformer.transformDefinition(jDefinition)));
			default -> new Tuple<>(Collections.emptyList(), new None<>());
		};
	}
}
