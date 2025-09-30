package magma.compile;

import java.util.List;

public class Lang {
	public sealed interface JavaRootSegment permits JavaClass, Content, JavaImport, JavaPackage {}

	public sealed interface CRootSegment permits CStructure, Content {}

	public sealed interface JavaClassSegment permits JavaBlock, JavaClass, Content, JavaStruct {}

	@Type("struct")
	public record CStructure(String name) implements CRootSegment {}

	public record JavaRoot(List<JavaRootSegment> children) {}

	public record CRoot(List<CRootSegment> children) {}

	@Type("class")
	public record JavaClass(String name, List<JavaClassSegment> children) implements JavaRootSegment, JavaClassSegment {}

	@Type("import")
	public record JavaImport(String content) implements JavaRootSegment {}

	@Type("package")
	public record JavaPackage(String content) implements JavaRootSegment {}

	@Type("content")
	public record Content(String input) implements JavaRootSegment, JavaClassSegment, CRootSegment {}

	@Type("struct")
	public record JavaStruct(String name) implements JavaClassSegment {}

	@Type("block")
	public record JavaBlock(String header, String content) implements JavaClassSegment {}
}
