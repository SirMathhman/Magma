# Changes Made to Implement "Error by Default When Compiling a Source File"

## Overview

This document describes the changes made to implement the requirement to "error by default when compiling a source file" in the Magma Java-to-C compiler.

## Changes Made

1. **Added `errorByDefault` flag to MapUtils class**
   - Added a static boolean flag `errorByDefault` that controls whether compilation should error by default
   - Set the flag to `true` by default to ensure compilation errors by default

2. **Modified processing logic to check the flag**
   - Updated the `getStringStringMap` method to check if the file extension is ".java" and if `errorByDefault` is true
   - If both conditions are met, it throws a CompilationException

3. **Added method to override the default behavior**
   - Added a public static method `setErrorByDefault(boolean value)` to allow overriding the default behavior
   - This method can be called with `false` to allow compilation to proceed normally

4. **Added CompilationException class**
   - Added a nested static class `CompilationException` that extends RuntimeException
   - This exception is thrown when compilation fails due to `errorByDefault` being true

5. **Updated Main class to demonstrate the behavior**
   - Modified the Main class to demonstrate both the default behavior (which fails) and how to override it
   - Added try-catch blocks to handle the expected CompilationException
   - Added code to override the default behavior and verify that it works

## How to Use

By default, when processing a Java file, the MapUtils class will throw a CompilationException with the message "Compilation failed: errorByDefault is set to true. Call setErrorByDefault(false) to override this behavior."

To override this behavior and allow compilation to proceed normally, call:

```java
MapUtils.setErrorByDefault(false);
```

This will disable the default error behavior and allow compilation to proceed normally.

## Testing

The implementation has been tested by:

1. Running the build script to verify that the code compiles without errors
2. Running the Main class to verify that:
   - By default, when processing a Java file, the MapUtils class throws a CompilationException
   - After calling MapUtils.setErrorByDefault(false), the processing succeeds

The output of running the Main class confirms that the implementation works as expected.