struct EscapingFolder(Folder folder) implements Folder{};
DivideState fold_EscapingFolder(Folder folder) implements Folder(DivideState state, char c) {}
char* delimiter_EscapingFolder(Folder folder) implements Folder() {}
DivideState foldEscape_EscapingFolder(Folder folder) implements Folder(Character tuple) {}
