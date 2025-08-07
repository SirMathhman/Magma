# CheckStyle Configuration for Magma

This document describes the CheckStyle configuration that has been added to the Magma project.

## Overview

CheckStyle is a development tool that helps programmers write Java code that adheres to a coding standard. The Magma project now includes a custom CheckStyle configuration that enforces the following rules:

1. **Maximum 10 lines per function**: Methods should be concise and focused on a single responsibility.
2. **Maximum 2 parameters per method**: Methods should have a limited number of parameters to reduce complexity.
3. **Maximum 2 constructors per class**: Classes should have a limited number of constructors to maintain simplicity.
4. **Maximum 1 loop per method**: Methods should contain at most one loop to reduce complexity.
5. **Maximum 10 methods per class**: Classes should have a limited number of methods to maintain cohesion.

## Configuration Files

The CheckStyle configuration consists of two main files:

1. `checkstyle.xml`: Contains the CheckStyle rules configuration.
2. `pom.xml`: Contains the Maven plugin configuration that references the CheckStyle rules.

## Running CheckStyle

To run CheckStyle on the Magma project, use the following command:

```
.\checkstyle-maven.bat
```

This will execute the Maven CheckStyle plugin, which will check all Java files in the project against the rules defined in `checkstyle.xml`.

## CheckStyle Rules Details

### Maximum 10 lines per function
```xml
<module name="MethodLength">
    <property name="tokens" value="METHOD_DEF"/>
    <property name="max" value="10"/>
    <property name="countEmpty" value="false"/>
</module>
```

### Maximum 2 parameters per method
```xml
<module name="ParameterNumber">
    <property name="max" value="2"/>
    <property name="tokens" value="METHOD_DEF, CTOR_DEF"/>
</module>
```

### Maximum 2 constructors per class
```xml
<module name="MethodCount">
    <property name="maxTotal" value="2"/>
    <property name="tokens" value="CTOR_DEF"/>
</module>
```

### Maximum 1 loop per method
```xml
<module name="NestedForDepth">
    <property name="max" value="0"/>
</module>
<module name="NestedWhileDepth">
    <property name="max" value="0"/>
</module>
<module name="NestedDoWhileDepth">
    <property name="max" value="0"/>
</module>
```

### Maximum 10 methods per class
```xml
<module name="MethodCount">
    <property name="maxTotal" value="10"/>
    <property name="tokens" value="CLASS_DEF"/>
</module>
```

## Troubleshooting

If you encounter issues running CheckStyle:

1. Ensure Maven is installed and in your PATH.
2. Check that the `checkstyle.xml` file is in the root directory of the project.
3. Verify that the `pom.xml` file contains the correct reference to the CheckStyle configuration.

## Additional Resources

- [CheckStyle Documentation](https://checkstyle.sourceforge.io/)
- [Maven CheckStyle Plugin Documentation](https://maven.apache.org/plugins/maven-checkstyle-plugin/)