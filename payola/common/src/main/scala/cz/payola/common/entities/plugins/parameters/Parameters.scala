package cz.payola.common.entities.plugins.parameters

import cz.payola.common.entities.plugins.Parameter

/**
  * A parameter with [[scala.Boolean]] value.
  */
trait BooleanParameter extends Parameter[Boolean]

/**
  * A parameter with [[scala.Float]] value.
  */
trait FloatParameter extends Parameter[Float]

/**
  * A parameter with [[scala.Int]] value.
  */
trait IntParameter extends Parameter[Int]

/**
  * A parameter with [[scala.String]] value.
  */
trait StringParameter extends Parameter[String] {
    protected val _isMultiline: Boolean
    
    def isMultiline: Boolean = _isMultiline
}
