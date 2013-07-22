package cz.payola.common.entities.plugins

import cz.payola.common.Entity

/**
  * A parameter of an analytical plugin.
  * @tparam A Type of the parameter value.
  */
trait Parameter[A] extends Entity
{
    protected val _name: String

    protected val _defaultValue: A

    protected val _ordering: Option[Int]

    override def classNameText = "parameter"

    /** Name of the parameter. */
    def name = _name

    /** Default value of the parameter. */
    def defaultValue = _defaultValue

    /** Parameter ordering */
    def ordering = _ordering
}
