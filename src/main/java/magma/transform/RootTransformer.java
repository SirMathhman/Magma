package magma.transform;

import magma.compile.Lang;
import magma.compile.error.CompileError;
import magma.result.Ok;
import magma.result.Result;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class RootTransformer {
	public static Result<Lang.CRoot, CompileError> transform(Lang.JRoot node) {
		final List<Lang.JavaRootSegment> children = node.children();
		final Stream<Lang.JavaRootSegment> stream = children.stream();
		final Stream<List<Lang.CRootSegment>> listStream = stream.map(Transformer::flattenRootSegment);
		final Stream<Lang.CRootSegment> cRootSegmentStream = listStream.flatMap(Collection::stream);
		final List<Lang.CRootSegment> newChildren = cRootSegmentStream.toList();
		return new Ok<>(new Lang.CRoot(newChildren));
	}
}
