import java.util.LinkedHashMap;
import java.util.Map;

class Compiler {
  public static String compile(String input) {
    boolean hasReadIntExtern = hasReadIntExtern(input);
    Map<String, String> functions = parseFunctions(input);
    String entry = findEntryPoint(input);
    return buildC(functions, entry, hasReadIntExtern);
  }

  private static boolean hasReadIntExtern(String input) {
    return input.contains("extern fn readInt");
  }

  private static Map<String, String> parseFunctions(String input) {
    Map<String, String> functions = new LinkedHashMap<>();
    int idx = 0;
    while (true) {
      int fnIndex = input.indexOf("fn ", idx);
      if (fnIndex == -1)
        break;

      int lastSemi = input.lastIndexOf(';', fnIndex);
      int tokenStart = (lastSemi == -1) ? 0 : lastSemi + 1;
      String prefix = input.substring(tokenStart, fnIndex);
      if (prefix.contains("extern")) {
        idx = fnIndex + 3;
        continue;
      }

      int nameStart = fnIndex + 3;
      int paren = input.indexOf('(', nameStart);
      if (paren == -1)
        break;
      String name = input.substring(nameStart, paren).trim();

      int arrow = input.indexOf("=>", paren);
      if (arrow == -1) {
        idx = paren + 1;
        continue;
      }
      int semi = input.indexOf(';', arrow);
      if (semi == -1) {
        idx = arrow + 2;
        continue;
      }

      String expr = input.substring(arrow + 2, semi).trim();
      functions.put(name, expr);
      idx = semi + 1;
    }
    return functions;
  }

  private static String findEntryPoint(String input) {
    String entry = null;
    int lastClose = input.lastIndexOf(')');
    if (lastClose != -1) {
      int open = input.lastIndexOf('(', lastClose);
      if (open != -1) {
        int i = open - 1;
        while (i >= 0 && Character.isWhitespace(input.charAt(i)))
          i--;
        int end = i + 1;
        while (i >= 0) {
          char c = input.charAt(i);
          if (Character.isLetterOrDigit(c) || c == '_')
            i--;
          else
            break;
        }
        int start = i + 1;
        if (start < end)
          entry = input.substring(start, end);
      }
    }
    return (entry == null) ? "main" : entry;
  }

  private static String buildC(Map<String, String> functions, String entry, boolean hasReadIntExtern) {
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n\n");

    if (hasReadIntExtern) {
      sb.append("int readInt(){ int x; if (scanf(\"%d\", &x) != 1) return 0; return x; }\n\n");
    }

    for (String fn : functions.keySet()) {
      sb.append("int ").append(fn).append("();\n");
    }
    sb.append("\n");

    for (Map.Entry<String, String> e : functions.entrySet()) {
      String name = e.getKey();
      String expr = e.getValue();
      sb.append("int ").append(name).append("(){ return ").append(expr).append("; }\n");
    }
    sb.append("\nint main(){ return ").append(entry).append("(); }\n");

    return sb.toString();
  }
}