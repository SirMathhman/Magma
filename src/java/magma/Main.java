package magma;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class Main {
  public static void main(String[] args) {
    Path srcRoot = Paths.get("src", "java");
    Path dstRoot = Paths.get("src", "windows");

    try {
      copyAndRenameToC(srcRoot, dstRoot);
      System.out.println("Copy complete.");
    } catch (IOException e) {
      System.err.println("Error during copy: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void copyAndRenameToC(Path srcRoot, Path dstRoot) throws IOException {
    if (!Files.exists(srcRoot) || !Files.isDirectory(srcRoot)) {
      throw new IOException("Source directory does not exist: " + srcRoot.toString());
    }

    try (Stream<Path> paths = Files.walk(srcRoot)) {
      paths.filter(Files::isRegularFile).forEach(source -> {
        try {
          Path relative = srcRoot.relativize(source);
          String fileName = relative.getFileName().toString();
          String newFileName = replaceExtensionWithC(fileName);

          Path targetDir = dstRoot.resolve(relative).getParent();
          if (targetDir == null) {
            targetDir = dstRoot;
          }
          Files.createDirectories(targetDir);

          Path target = targetDir.resolve(newFileName);
          Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
          System.out.println("Copied: " + source + " -> " + target);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      });
    } catch (UncheckedIOException e) {
      throw e.getCause();
    }
  }

  private static String replaceExtensionWithC(String fileName) {
    int lastDot = fileName.lastIndexOf('.');
    if (lastDot <= 0) { // no dot or dot is the first char
      return fileName + ".c";
    } else {
      return fileName.substring(0, lastDot) + ".c";
    }
  }
}
