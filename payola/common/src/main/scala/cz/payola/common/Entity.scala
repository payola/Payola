package cz.payola.common

/**
  * A generic entity.
  */
trait Entity
{
    /** Unique id of the entity. */
    val id: String

    /** Returns name of the entity class. */
    def className: String = Entity.getClassName(getClass)

    /** Name of the entity type that is presentable to the user of the application. */
    def classNameText: String = className.toLowerCase

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

object Entity
{
    /**
     * Returns an entity class name corresponding to the specified entity class.
     */
    def getClassName(entityClass: Class[_]): String = {
        val className = entityClass.getName
        className.drop(className.lastIndexOf(".") + 1)
    }
}
