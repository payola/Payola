package cz.payola.common

/**
  * A generic entity.
  */
trait Entity
{
    /** Unique id of the entity. */
    val id: String

    /**
      * Checks whether the specified value is true. If not, throws a new [[cz.payola.common.ValidationException]].
      * @param value The value to check.
      * @param fieldName Name of the field that caused the error.
      * @param message Additional message.
      */
    protected def validate(value: Boolean, fieldName: String, message: String) {
        if (!value) {
            throw new ValidationException(fieldName, message)
        }
    }
}
