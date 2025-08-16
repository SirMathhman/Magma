#include <string.h>
#include <stdbool.h>
#include <stdint.h>
bool isLetter(const uint8_t* ch){if(strlen(ch) == 0){return false;}uint8_t code = ch[0];return (code >= 65 && code <= 90) || (code >= 97 && code <= 122);} bool isDigit(const uint8_t* ch){if(strlen(ch) == 0){return false;}uint8_t code = ch[0];return code >= 48 && code <= 57;}