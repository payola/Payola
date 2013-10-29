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
trait StringParameter extends Parameter[String]
{
    protected val _isMultiline: Boolean

    protected val _isPattern: Boolean

    protected val _isPassword: Boolean

    protected val _canContainUrl: Boolean

    def isPattern: Boolean = _isPattern

    def isMultiline: Boolean = _isMultiline

    def isPassword: Boolean = _isPassword

    def canContainUrl: Boolean = _canContainUrl
}


