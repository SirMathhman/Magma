package magma.transform;

import magma.compile.Lang;
import magma.option.Option;
import magma.option.Some;

import java.util.ArrayList;
import java.util.List;

public class StructureTransformer {
	static List<Lang.CRootSegment> flattenStructure(Lang.JStructure aClass) {
		final List<Lang.JStructureSegment> children = aClass.children();

		final ArrayList<Lang.CRootSegment> segments = new ArrayList<>();
		final ArrayList<Lang.CDefinition> fields = new ArrayList<>();

		// Special handling for Record params - add them as struct fields
		addRecordParamsAsFields(aClass, fields);

		final String name = aClass.name();
		children.stream().map(child -> StructureSegmentTransformer.flattenStructureSegment(child, name)).forEach(tuple -> {
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
}
