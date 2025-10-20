// File generated from 'JavaPath[path=.\src\main\java\magma\JavaImpl.java]'. This is not source code!
#include "Main.h"
char* display_JIOError(){
	StringWriter writer = new_StringWriter();
	this.e.printStackTrace(new_PrintWriter(writer));
	return writer.toString();
}
Result<char*, IOError> readString_JavaPath(){
	/*try {
				return new Ok<String, IOError>(Files.readString(this.path));
			}*/
	/*catch (IOException e) {
				return new Err<String, IOError>(new JIOError(e));
			}*/
}
Option<IOError> createDirectories_JavaPath(){
	/*try {
				Files.createDirectories(this.path);
				return new None<IOError>();
			}*/
	/*catch (IOException e) {
				return new Some<IOError>(new JIOError(e));
			}*/
}
Option<IOError> writeString_JavaPath(char* output){
	/*try {
				Files.writeString(this.path, output);
				return new None<IOError>();
			}*/
	/*catch (IOException e) {
				return new Some<IOError>(new JIOError(e));
			}*/
}
Path getParent_JavaPath(){
	return new_JavaPath(this.path.getParent());
}
Result<ArrayList<Path>, IOError> walk_JavaPath(){
	/*try (final Stream<java.nio.file.Path> stream = Files.walk(this.path)) {
				final Path[] array = stream.map(JavaPath::new).toArray(Path[]::new);
				return new Ok<ArrayList<Path>, IOError>(Streams.fromRef(array).collect(new ListCollector<Path>()));
			}*/
	/*catch (IOException e) {
				return new Err<ArrayList<Path>, IOError>(new JIOError(e));
			}*/
}
char* asString_JavaPath(){
	return this.path.toString();
}
Path relativize_JavaPath(Path path){
	/*java.nio.file.Path*/ fold = this.unwrap(path);
	return new_JavaPath(this.path.relativize(fold));
}
auto __lambda0__() {
	return java.nio.file.Paths.get(".");
}
/*java.nio.file.Path*/ unwrap_JavaPath(Path path){
	return path.stream().map(get_/*java.nio.file.Paths*/).fold(resolve_/*java.nio.file.Path*/).orElseGet(__lambda0__);
}
Path resolveByPath_JavaPath(Path path){
	return new_JavaPath(path.stream().foldWithInitial(this.path, resolve_/*java.nio.file.Path*/));
}
Streams.Stream<char*> stream_JavaPath(){
	int length = this.path.getNameCount();
	return Streams.fromLength(length).map(getName_/*this.path*/).map(toString_/*java.nio.file.Path*/);
}
Path getFileName_JavaPath(){
	return new_JavaPath(this.path.getFileName());
}
Path resolveByString_JavaPath(char* name){
	return new_JavaPath(this.path.resolve(name));
}
boolean exists_JavaPath(){
	return Files.exists(this.path);
}
Path get_Paths(char* first, /*String...*/ more);
int main(){
	main_Main();
	return 0;
}