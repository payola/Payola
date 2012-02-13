package cz.payola.scala2json.annotations;

import java.lang.annotation.*;

/**
 * Allows the field to use another name during the serialization process.
 *
 * E.g. @JSONFieldName(name = "heaven") var hell: Double = 33.0d will
 * be transformed to heaven: 33.0.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSONFieldName {
    public String name();
}
