package cz.payola.common.entities.analyses

/**
  * A parameter of an analytical plugin.
  * @tparam A Type of the parameter value.
  */
trait Parameter[A]
{
    /** Name of the parameter. */
    val name: String
}
