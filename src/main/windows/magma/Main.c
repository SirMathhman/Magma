/*public class Main {
	public static void main(String[] args) {
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");*//*final String input = Files.readString(source);*//*final Path target = Paths.get(".", "src", "main", "windows", "magma", "Main.c");*//*final Path targetParent = target.getParent();*//*if (!Files.exists(targetParent)) Files.createDirectories(targetParent);*//*Files.writeString(target, compile(input));*//*} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();*//*}
	}

	private static String compile(String input) {
		final ArrayList<String> segments = new ArrayList<String>();*//*StringBuilder buffer = new StringBuilder();*//*for (int index = 0;*//*index < input.length();*//*index++) {
			final char c = input.charAt(index);*//*buffer.append(c);*//*if (c == ';*//*') {
				segments.add(buffer.toString());*//*buffer = new StringBuilder();*//*}
		}
		segments.add(buffer.toString());*//*final String joined = segments.stream().map(Main::compileRootSegment).collect(Collectors.joining());*//*return joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();*//*" + System.lineSeparator() +
					 "\treturn 0;*//*" + System.lineSeparator() + "}";*//*}

	private static String compileRootSegment(String input) {
		final String stripped = input.strip();*//*if (stripped.startsWith("package ") || stripped.startsWith("import ")) return "";*//*return wrap(stripped);*//*}

	private static String wrap(String input) {
		final String replaced = input.replace("start", "start").replace("end", "end");*//*return "start" + replaced + "end";*//*}
}*/int main(){
	main_Main();
	return 0;
}