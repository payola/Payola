package cz.payola.common.entities.analyses

/**
  * A parameter of an analytical plugin.
  * @tparam A Type of the parameter value.
  */
trait Parameter[A]
{
    protected val _name: String

    protected var _defaultValue: A

    /** Name of the parameter. */
    def name  = _name

    /** Default value for parameter **/
    def defaultValue = _defaultValue

    /**
      * Sets default value of the parameter.
      * @param value The new parameter default value.
      */
    protected def defaultValue_=(value: A) {
        _defaultValue = value
    }
}
