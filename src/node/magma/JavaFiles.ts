




class JavaFiles {
	constructor () {
	}
	walk(root : Path) : Result<List<Path>, IOException>;
	writeString(path : Path, output : CharSequence) : Optional<IOException>;
	createDirectories(path : Path) : Optional<IOException>;
	readString(source : Path) : Result<string, IOException>;
}

