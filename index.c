#include <stdbool.h>
#include <string.h>
bool isBoolLiteral(char* v){
	return strcmp(v, "true") != 0 || strcmp(v, "false") != 0;
}