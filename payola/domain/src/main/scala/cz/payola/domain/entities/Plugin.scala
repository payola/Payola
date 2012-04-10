package cz.payola.domain.entities

import collection.mutable.ArrayBuffer
import generic.{ConcreteEntity, ConcreteNamedEntity}
import parameters.Parameter

class Plugin(
        id: String = java.util.UUID.randomUUID.toString,
        protected var _name: String)
    extends ConcreteEntity(id)
    with ConcreteNamedEntity
    with cz.payola.common.entities.Plugin
{
    type ParameterType = Parameter[_]

    // Parameters. Doesn't need a setter as all we need to check is that it's not null
    private val _parameterIDs: ArrayBuffer[String] = new ArrayBuffer[String]()

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
            _parameterIDs += p.id
            _parameters += p
        }
    }

    /** Checks whether the parameter is in the parameter list or not.
      *
      * @param p The parameter.
      *
      * @return True or false.
      */
    def containsParameter(p: ParameterType): Boolean = _parameterIDs.contains(p.id)

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
    def parameterCount: Int = _parameterIDs.size

    /** Returns an immutable copy of the parameter array.
      *
      * @return Immutable copy of the parameter array.
      */
    /*def parameters = {
        val params = List[ParameterType]()
        _parameterIDs foreach { paramID: String =>
            val p: Option[ParameterType] = _cachedParameters.get(paramID)
            if (p.isEmpty){
                // TODO loading from DB
            }else{
                p.get :: params
            }
        }
        params.reverse
    }*/

    /** Removes a parameter from the parameter list.
      *
      * @param p The parameter to be remove.
      *
      * @throws IllegalArgumentException if the plugin's parameter array
      *             doesn't contain this parameter.
      */
    def removeParameter(p: ParameterType) = {
        require(containsParameter(p), "Cannot remove a parameter that isn't a member of this plugin!")
        _parameterIDs -= p.id
        _parameters -= p
    }
}
