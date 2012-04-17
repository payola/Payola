package cz.payola.domain.entities

import cz.payola._
import collection.mutable.ArrayBuffer
import generic.ConcreteNamedEntity
import parameters.Parameter

class Plugin(protected var _name: String) extends common.entities.Plugin with ConcreteNamedEntity
{
    type ParameterType = Parameter[_]

    // Parameters. Doesn't need a setter as all we need to check is that it's not null
    protected val _parameters: ArrayBuffer[ParameterType] = new ArrayBuffer[ParameterType]()

    /** Adds a new parameter to the parameter list.
      *
      * @param p Parameter to be added?
      *
      * @throws IllegalArgumentException if the parameter is null.
      */
    def addParameter(p: ParameterType) = {
        require(p != null, "Cannot add null parameter!")
        if (!containsParameter(p)) {
            _parameters += p
        }
    }

    /** Checks whether the parameter is in the parameter list or not.
      *
      * @param p The parameter.
      *
      * @return True or false.
      */
    def containsParameter(p: ParameterType): Boolean = _parameters.contains(p)

    /** Returns a parameter at index.
      *
      * @param index Index of the parameter.
      * @return Parameter at index.
      */
    def parameterAtIndex(index: Int): ParameterType = {
        require(index >= 0 && index < parameterCount, "Parameter index out of bounds - " + index)
        _parameters(index)
    }

    /** Returns number of parameters.
      *
      * @return Number of parameters.
      */
    def parameterCount: Int = _parameters.size

    /** Removes a parameter from the parameter list.
      *
      * @param p The parameter to be remove.
      *
      * @throws IllegalArgumentException if the plugin's parameter array
      *             doesn't contain this parameter.
      */
    def removeParameter(p: ParameterType) = {
        require(containsParameter(p), "Cannot remove a parameter that isn't a member of this plugin!")
        _parameters -= p
    }
}
