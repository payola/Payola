package cz.payola.scala2json.annotations;

import java.lang.annotation.*;

/**
 *  Allows the class to specify a different class name for the __class__ field.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JSONPoseableClass {
    public String otherClassName();
}
