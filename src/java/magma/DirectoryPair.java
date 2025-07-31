package magma;

import java.nio.file.Path;

/**
 * A record that combines both the source and target directories.
 * This is used to simplify method signatures by passing a single object
 * instead of separate source and target directory parameters.
 *
 * @param sourceDir the source directory containing Java files
 * @param targetDir the target directory for TypeScript files
 */
record DirectoryPair(Path sourceDir, Path targetDir) {
    /**
     * Creates a new DirectoryPair with the specified source and target directories.
     *
     * @param sourceDir the source directory containing Java files
     * @param targetDir the target directory for TypeScript files
     */
    public DirectoryPair {
        // Compact constructor for validation if needed
        if (null == sourceDir) throw new IllegalArgumentException("Source directory cannot be null");
        if (null == targetDir) throw new IllegalArgumentException("Target directory cannot be null");
    }
}