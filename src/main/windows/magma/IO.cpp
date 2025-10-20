// File generated from 'JavaPath[path=.\src\main\java\magma\IO.java]'. This is not source code!
#include "Main.h"
char* display_IOError();
boolean exists_Path();
Result<char*, IOError> readString_Path();
Option<IOError> createDirectories_Path();
Option<IOError> writeString_Path(char* output);
Path getParent_Path();
Result<ArrayList<Path>, IOError> walk_Path();
char* asString_Path();
Path relativize_Path(Path path);
Path resolveByPath_Path(Path path);
Stream<char*> stream_Path();
Path getFileName_Path();
Path resolveByString_Path(char* name);
int main(){
	main_Main();
	return 0;
}