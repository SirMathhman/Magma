package magma;

enum JavaType {
	INT("number"), LONG("number"), FLOAT("number"), DOUBLE("number"), BYTE("number"), SHORT("number"), BOOLEAN("boolean"),
	CHAR("string"), STRING("string"), OBJECT("any"), VOID("void");

	private final String typeScriptType;

	JavaType(final String typeScriptType) {
		this.typeScriptType = typeScriptType;
	}

	public static String getTypeScriptType((javaType: string) {
		try {
			return JavaType.valueOf(javaType.toUpperCase()).typeScriptType;
		} catch (final IllegalArgumentException e) {
			return javaType;
		}
	}
}