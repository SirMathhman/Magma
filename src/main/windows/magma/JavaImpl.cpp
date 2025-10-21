// File generated from '.\src\main\java\magma\JavaImpl.java'. This is not source code!
#include "Main.h"
char* display_JIOError(void* _ref){
	StringWriter writer = new_StringWriter();
	this.e.printStackTrace(new_PrintWriter(writer));
	return writer.toString();
}
auto __lambda0__() {
	return java.nio.file.Paths.get(".");
}
/*java.nio.file.Path*/ unwrap_JavaPath(void* _ref, Path path){
	return path.stream().map(get_/*java.nio.file.Paths*/).fold(resolve_/*java.nio.file.Path*/).orElseGet(__lambda0__);
}
Result<char*, IOError> readString_JavaPath(void* _ref){
	/*try {
				return new Ok<String, IOError>(Files.readString(this.path));
			}*/
	/*catch (IOException e) {
				return new Err<String, IOError>(new JIOError(e));
			}*/
}
Option<IOError> createDirectories_JavaPath(void* _ref){
	/*try {
				Files.createDirectories(this.path);
				return new None<IOError>();
			}*/
	/*catch (IOException e) {
				return new Some<IOError>(new JIOError(e));
			}*/
}
Option<IOError> writeString_JavaPath(void* _ref, char* output){
	/*try {
				Files.writeString(this.path, output);
				return new None<IOError>();
			}*/
	/*catch (IOException e) {
				return new Some<IOError>(new JIOError(e));
			}*/
}
Path getParent_JavaPath(void* _ref){
	return new_JavaPath(this.path.getParent());
}
Result<ArrayList<Path>, IOError> walk_JavaPath(void* _ref){
	/*try (final Stream<java.nio.file.Path> stream = Files.walk(this.path)) {
				final Path[] array = stream.map(JavaPath::new).toArray(Path[]::new);
				return new Ok<ArrayList<Path>, IOError>(Streams.fromRef(array).collect(new ListCollector<Path>()));
			}*/
	/*catch (IOException e) {
				return new Err<ArrayList<Path>, IOError>(new JIOError(e));
			}*/
}
char* asString_JavaPath(void* _ref){
	return this.path.toString();
}
Path relativize_JavaPath(void* _ref, Path path){
	/*java.nio.file.Path*/ fold = JavaPath.unwrap(path);
	return new_JavaPath(this.path.relativize(fold));
}
Path resolveByPath_JavaPath(void* _ref, Path path){
	return new_JavaPath(path.stream().foldWithInitial(this.path, resolve_/*java.nio.file.Path*/));
}
Streams.Stream<char*> stream_JavaPath(void* _ref){
	int length = this.path.getNameCount();
	return Streams.fromLength(length).map(getName_/*this.path*/).map(toString_/*java.nio.file.Path*/);
}
Path getFileName_JavaPath(void* _ref){
	return new_JavaPath(this.path.getFileName());
}
Path resolveByString_JavaPath(void* _ref, char* name){
	return new_JavaPath(this.path.resolve(name));
}
boolean exists_JavaPath(void* _ref){
	return Files.exists(this.path);
}
int main(){
	main_Main();
	return 0;
}