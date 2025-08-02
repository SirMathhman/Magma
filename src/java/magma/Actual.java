package magma;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks methods that perform actual I/O operations or have side effects.
 * 
 * This annotation is used to distinguish methods that interact with the external world
 * (file system, network, etc.) from pure functions that only transform data without
 * side effects. Methods marked with this annotation may throw exceptions related to
 * I/O operations and should be handled accordingly.
 * 
 * In the Magma project, this annotation is typically used on methods that read from
 * or write to files, such as {@code readString} and {@code writeString} in the
 * {@code Main} class.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Actual {}
