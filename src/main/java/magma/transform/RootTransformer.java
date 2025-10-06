package magma.transform;

import magma.Tuple;
import magma.compile.Lang;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Ok;
import magma.result.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class RootTransformer {
	public static Result<Lang.CRoot, CompileError> transform(Lang.JRoot node) {
		final List<Lang.JavaRootSegment> children = node.children();
		final Stream<Lang.JavaRootSegment> stream = children.stream();
		final Stream<List<Lang.CRootSegment>> listStream = stream.map(RootTransformer::flattenRootSegment);
		final Stream<Lang.CRootSegment> cRootSegmentStream = listStream.flatMap(Collection::stream);
		final List<Lang.CRootSegment> newChildren = cRootSegmentStream.toList();
		return new Ok<>(new Lang.CRoot(newChildren));
	}

	private static List<Lang.CRootSegment> flattenRootSegment(Lang.JavaRootSegment segment) {
		return switch (segment) {
			case Lang.JStructure jStructure -> flattenStructure(jStructure);
			case Lang.Invalid invalid -> List.of(invalid);
			default -> Collections.emptyList();
		};
	}

	private static List<Lang.CRootSegment> flattenStructure(Lang.JStructure aClass) {
		final List<Lang.JStructureSegment> children = aClass.children();

		final ArrayList<Lang.CRootSegment> segments = new ArrayList<>();
		final ArrayList<Lang.CDefinition> fields = new ArrayList<>();

		// Special handling for Record params - add them as struct fields
		addRecordParamsAsFields(aClass, fields);

		final String name = aClass.name();
		children.stream().map(child -> flattenStructureSegment(child, name)).forEach(tuple -> {
			segments.addAll(tuple.left());
			if (tuple.right() instanceof Some<Lang.CDefinition>(Lang.CDefinition value)) fields.add(value);
		});

		final Lang.Structure structure =
				new Lang.Structure(name, fields, new Some<>(System.lineSeparator()), aClass.typeParameters());
		final List<Lang.CRootSegment> copy = new ArrayList<>();
		copy.add(structure);
		copy.addAll(segments);
		return copy;
	}

	private static void addRecordParamsAsFields(Lang.JStructure aClass, ArrayList<Lang.CDefinition> fields) {
		if (aClass instanceof Lang.RecordNode record) {
			Option<List<Lang.JDefinition>> params = record.params();
			if (params instanceof Some<List<Lang.JDefinition>>(List<Lang.JDefinition> paramList))
				paramList.stream().map(Transformer::transformDefinition).forEach(fields::add);
		}
	}

	private static Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>> flattenStructureSegment(Lang.JStructureSegment self,
																																																	String name) {
		return switch (self) {
			case Lang.Invalid invalid -> new Tuple<>(List.of(invalid), new None<>());
			case Lang.Method method -> new Tuple<>(List.of(Transformer.transformMethod(method, name)), new None<>());
			case Lang.JStructure jClass -> new Tuple<>(flattenStructure(jClass), new None<>());
			case Lang.Field field ->
					new Tuple<>(Collections.emptyList(), new Some<>(Transformer.transformDefinition(field.value())));
			case Lang.Whitespace _, Lang.LineComment _, Lang.BlockComment _ ->
					new Tuple<>(Collections.emptyList(), new None<>());
			case Lang.JInitialization jInitialization -> new Tuple<>(Collections.emptyList(),
																															 new Some<>(Transformer.transformDefinition(
																																	 jInitialization.definition())));
			case Lang.JDefinition jDefinition ->
					new Tuple<>(Collections.emptyList(), new Some<>(Transformer.transformDefinition(jDefinition)));
		};
	}
}
