package cz.payola.common.entities.analyses

/**
  * A parameter of an analytical plugin.
  * @tparam A Type of the parameter value.
  */
trait Parameter[A]
{
    protected val _name: String

    protected val _defaultValue: A

    /** Name of the parameter. */
    def name = _name

    /** Default value of the parameter. */
    def defaultValue = _defaultValue
}
