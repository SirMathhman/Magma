package magma;

sealed interface MethodHeader permits Assignable, Constructor {
    String generateWithAfterName(String afterName);
}
