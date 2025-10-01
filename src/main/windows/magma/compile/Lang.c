struct Lang {};/*
	sealed public interface JavaRootSegment {}*//*

	sealed public interface CRootSegment {}*//*

	public sealed interface JavaClassMember {}*//*

	sealed public interface JavaType {}*//*

	sealed public interface CType {}*//*

	@Tag("generic")
	public record Generic(String base, List<JavaType> arguments) implements JavaType {}*//*

	@Tag("definition")
	public record JavaDefinition(String name, JavaType type) {}*//*

	@Tag("method")
	public record Method(JavaDefinition definition, Option<List<JavaDefinition>> params, String body)
			implements JavaClassMember {}*//*

	@Tag("content")
	public record Content(String value) implements JavaRootSegment, JavaClassMember, CRootSegment, JavaType, CType {}*//*

	@Tag("class")
	public record JClass(Option<String> modifiers, String name, List<JavaClassMember> children)
			implements JavaRootSegment {}*//*

	@Tag("interface")
	public record Interface(Option<String> modifiers, String name, List<JavaClassMember> children)
			implements JavaRootSegment {}*//*

	@Tag("record")
	public record Record(Option<String> modifiers, String name, List<JavaClassMember> children)
			implements JavaRootSegment {}*//*

	@Tag("struct")
	public record Structure(String name) implements CRootSegment {}*//*

	@Tag("whitespace")
	public record Whitespace() implements JavaRootSegment, JavaClassMember {}*//*

	public record JavaRoot(List<JavaRootSegment> children) {}*//*

	public record CRoot(List<CRootSegment> children) {}*//*

	@Tag("import")
	public record Import(String value) implements JavaRootSegment {}*//*

	@Tag("package")
	public record Package(String value) implements JavaRootSegment {}*//*

	@Tag("definition")
	public record CDefinition(String name, CType type) {}*//*

	@Tag("function")
	public record Function(CDefinition definition, List<CDefinition> params, String body) implements CRootSegment {}*//*

	@Tag("identifier")
	public record Identifier(String value) implements JavaType, CType {}*/Rule createCRootRuleRule FunctionRule CTypeRule StructRule createJavaRootRuleRule Whitespace/*

	private static Rule Namespace(String type) {
		return Tag(type, Strip(Prefix(type + " ", Suffix(Content(), ";"))));
	}*//*

	private static Rule Structure(String type) {
		final Rule modifiers = String("modifiers");
		final Rule name = String("name");
		final Rule children = Statements("children", ClassMember());

		final Rule aClass = First(First(Strip(Or(modifiers, Empty)), type + " ", name), "{", children);
		return Tag(type, Strip(Suffix(aClass, "}")));
	}*/Rule ClassMember/*

	private static Rule Method(Rule params) {
		final Rule header = Strip(Suffix(Last(Node("definition", Definition()), "(", params), ")"));
		return Tag("method", Strip(Suffix(First(header, "{", String("body")), "}")));
	}*/Rule DefinitionRule JavaTypeRule IdentifierRule GenericRule Content