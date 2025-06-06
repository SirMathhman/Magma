package magma;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main() throws Exception {
        Path source = Paths.get("src/magma/Main.java");
        Path target = Paths.get("src/magma/Main.mgs");

        String content = Files.readString(source);
        Pattern pattern = Pattern.compile("import\\s+([\\w\\.]+);", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String full = matcher.group(1);
            int last = full.lastIndexOf('.');
            String cls = full.substring(last + 1);
            String replacement = "import " + cls + " from " + full + ";";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        Files.writeString(target, sb.toString());
    }
}
