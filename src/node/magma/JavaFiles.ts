




class JavaFiles {
	constructor () {
	}
	static walk(final root : Path) : Result<List<Path>, IOException>;
	static writeString(final path : Path, final output : CharSequence) : Optional<IOException>;
	static createDirectories(final path : Path) : Optional<IOException>;
	static readString(final source : Path) : Result<string, IOException>;
}

