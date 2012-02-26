package cz.payola.scala2json.annotations;

import java.lang.annotation.*;

/**
 *  Allows a field that is an array (implements the Traversable trait) to include the class
 *  name during the serialization process.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSONConcreteArrayClass {
    public String className();
}
