




class JavaFiles {
	constructor () {
	}
	walk(final root : Path) : Result<List<Path>, IOException>;
	writeString(final path : Path, final output : CharSequence) : Optional<IOException>;
	createDirectories(final path : Path) : Optional<IOException>;
	readString(final source : Path) : Result<string, IOException>;
}

