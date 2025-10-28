#include "Native.h"

bool isEmpty_char_ref(char **str)
{
	if (str == nullptr || *str == nullptr)
	{
		return true;
	}

	return std::string(*str).empty();
}