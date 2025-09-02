package magma.compiler;

import magma.ast.VarDecl;
import magma.parser.ParseResult;

public final class IsOperatorProcessor {
  private IsOperatorProcessor() {
  }

  public static String convertForJs(Compiler compiler, String src) {
    if (src == null || src.isEmpty())
      return src;
    return processIsOperator(compiler, src, (left, resolved, pr) -> {
      if ("I32".equals(resolved)) {
        return "(typeof (" + left + ") === 'number')";
      } else if ("Bool".equals(resolved)) {
        return "(typeof (" + left + ") === 'boolean')";
      }
      return "(false)";
    }, null);
  }

  public static String convertForC(Compiler compiler, String src, ParseResult pr) {
    if (src == null || src.isEmpty())
      return src;
    return processIsOperator(compiler, src, (left, resolved, parsed) -> {
      // Prefer the initializer's inferred type for runtime checks when available.
      var vd = findVarDecl(parsed, left);
      if (vd != null) {
        var rhs = vd.rhs() == null ? "" : vd.rhs().trim();
        if (!rhs.isEmpty()) {
          var actual = Semantic.exprType(compiler, rhs, parsed.decls());
          if (actual != null) {
            var act = actual;
            while (compiler.typeAliases.containsKey(act))
              act = compiler.typeAliases.get(act);
            if (act.equals(resolved))
              return "(1==1)";
            else
              return "(0==1)";
          }
        }
      }
      // fallback: use declared type (resolve aliases and unions)
      String declared = null;
      if (parsed != null) {
        for (var d : parsed.decls()) {
          if (d.name().equals(left)) {
            declared = d.type();
            break;
          }
        }
      }
      if (declared != null) {
        while (compiler.typeAliases.containsKey(declared))
          declared = compiler.typeAliases.get(declared);
        if (declared.contains("|")) {
          for (var part : declared.split("\\|")) {
            var p = part.trim();
            var prResolved = p;
            while (compiler.typeAliases.containsKey(prResolved))
              prResolved = compiler.typeAliases.get(prResolved);
            if (prResolved.equals(resolved))
              return "(1==1)";
          }
        } else if (!declared.isEmpty() && declared.equals(resolved)) {
          return "(1==1)";
        }
      }
      return "(0==1)";
    }, pr);
  }

  private interface Replacer {
    String replace(String left, String resolved, ParseResult pr);
  }

  private static String processIsOperator(Compiler compiler, String src, Replacer replacer, ParseResult pr) {
    if (src == null || src.isEmpty())
      return src;
    var out = new StringBuilder();
    int idx = 0;
    while (true) {
      var pos = CompilerUtil.findStandaloneTokenIndex(src, "is", idx);
      if (pos == -1) {
        out.append(src.substring(idx));
        break;
      }
      // ensure top-level
      if (!CompilerUtil.isTopLevelPos(src, pos)) {
        out.append(src.substring(idx, pos + 2));
        idx = pos + 2;
        continue;
      }
      // find left identifier (allow whitespace before)
      var leftIdent = CompilerUtil.identifierLeftOf(src, pos - 1);
      if (leftIdent == null) {
        out.append(src.substring(idx, pos + 2));
        idx = pos + 2;
        continue;
      }
      // find right type identifier
      var j = CompilerUtil.skipWhitespace(src, pos + 2);
      var k = j;
      while (k < src.length() && CompilerUtil.isIdentifierChar(src.charAt(k)))
        k++;
      if (k == j) {
        out.append(src.substring(idx, pos + 2));
        idx = pos + 2;
        continue;
      }
      var typeName = src.substring(j, k);
      var resolved = typeName;
      if (compiler.typeAliases.containsKey(resolved))
        resolved = compiler.typeAliases.get(resolved);
      var replacement = replacer.replace(leftIdent, resolved, pr);
      // append prefix up to start of leftIdent
      var leftStart = src.indexOf(leftIdent, pos - 1 - leftIdent.length() + 1);
      if (leftStart < idx)
        leftStart = src.indexOf(leftIdent, idx);
      if (leftStart == -1)
        leftStart = pos; // fallback
      out.append(src.substring(idx, leftStart));
      out.append(replacement);
      idx = k;
    }
    return out.toString();
  }

  private static VarDecl findVarDecl(ParseResult pr, String name) {
    if (pr == null)
      return null;
    for (var vd : pr.decls()) {
      if (vd.name().equals(name))
        return vd;
    }
    return null;
  }
}
