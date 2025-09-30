package magma.compile;

import magma.compile.rule.DivideRule;
import magma.compile.rule.InfixRule;
import magma.compile.rule.NodeRule;
import magma.compile.rule.OrRule;
import magma.compile.rule.PlaceholderRule;
import magma.compile.rule.PrefixRule;
import magma.compile.rule.Rule;
import magma.compile.rule.StringRule;
import magma.compile.rule.StripRule;
import magma.compile.rule.SuffixRule;
import magma.compile.rule.TypeRule;

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

	public static Rule createClassRule() {
		final NodeRule header = new NodeRule("header", createClassHeaderRule());
		final DivideRule children = new DivideRule("children", createJavaClassSegmentRule());
		return new TypeRule("class", new SuffixRule(new InfixRule(header, "{", children), "}"));
	}

	public static Rule createCRootRule() {
		return new DivideRule("children", createCRootSegmentRule());
	}

	private static Rule createCRootSegmentRule() {
		return new OrRule(List.of(new SuffixRule(createClassSegmentRule(), System.lineSeparator()), createContentRule()));
	}

	private static Rule createJavaClassSegmentRule() {
		return new StripRule(createClassSegmentRule());
	}

	private static Rule createClassSegmentRule() {
		return new OrRule(List.of(createStructHeaderRule(), createBlockRule(), createContentRule()));
	}

	public static Rule createContentRule() {
		return new TypeRule("content", new PlaceholderRule(new StringRule("input")));
	}

	private static Rule createBlockRule() {
		return new TypeRule("block", new SuffixRule(new InfixRule(new PlaceholderRule(new StringRule("header")), "{",
				new PlaceholderRule(new StringRule("content"))), "}"));
	}

	private static Rule createStructHeaderRule() {
		return new TypeRule("struct", new PrefixRule("struct ", new SuffixRule(new StringRule("name"), " {};")));
	}

	private static Rule createClassHeaderRule() {
		return new InfixRule(new StringRule("temp"), "class ", new StripRule(new StringRule("name")));
	}

	public static Rule createJavaRootRule() {
		return new DivideRule("children", createJavaRootSegmentRule());
	}

	private static Rule createJavaRootSegmentRule() {
		return new StripRule(new OrRule(
				List.of(createClassRule(), createPrefixRule("package"), createPrefixRule("import"),
								createContentRule())));
	}

	private static Rule createPrefixRule(String type) {
		return new TypeRule(type, new PrefixRule(type + " ", new StringRule("content")));
	}
}
