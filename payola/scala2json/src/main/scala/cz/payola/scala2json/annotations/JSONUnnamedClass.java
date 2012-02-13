package cz.payola.scala2json.annotations;

import java.lang.annotation.*;

/**
 * Allows the class to skip the __class__ field during the serialization.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSONUnnamedClass {

}
