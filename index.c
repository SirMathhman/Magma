#include <string.h>
#include <stdbool.h>
#include <stdint.h>
bool isLetter(char* ch){if(strlen(ch) == 0){return false;}int32_t code = ch[0];return (code >= 65 && code <= 90) || (code >= 97 && code <= 122);}