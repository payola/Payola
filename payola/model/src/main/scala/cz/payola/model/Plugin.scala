package cz.payola.model

import cz.payola._
import cz.payola.model.parameter._
import collection.mutable.{HashMap, ArrayBuffer}
import generic.ConcreteNamedEntity

class Plugin(n: String) extends common.model.Plugin with ConcreteNamedEntity
{
    type ParameterType = Parameter[_]

    // Parameters. Doesn't need a setter as all we need to check is that it's not null
    private val _parameterIDs: ArrayBuffer[String] = new ArrayBuffer[String]()
    private val _cachedParameters: HashMap[String, Parameter[_]] = new HashMap[String, Parameter[_]]()

    setName(n)

    /** Adds a new parameter to the parameter list.
     *
     * @param p Parameter to be added?
     *
     * @throws IllegalArgumentException if the parameter is null.
     */
    def addParameter(p: Parameter[_]) = {
        require(p != null, "Cannot add null parameter!")
        if (!containsParameter(p)){
            _parameterIDs += p.id
            _cachedParameters.put(p.id, p)
        }
    }

    /** Checks whether the parameter is in the parameter list or not.
     *
     * @param p The parameter.
     *
     * @return True or false.
     */
    def containsParameter(p: Parameter[_]): Boolean = _parameterIDs.contains(p.id)

    /** Returns number of parameters.
      *
      * @return Number of parameters.
      */
    def numberOfParameters: Int = _parameterIDs.size

    /** Returns a parameter at index.
      *
      * @param index Index of the parameter.
      * @return Parameter at index.
      */
    def parameterAtIndex(index: Int): Parameter[_] = {
        require(index >= 0 && index < numberOfParameters, "Parameter index out of bounds - " + index)
        val opt: Option[Parameter[_]] = _cachedParameters.get(_parameterIDs(index))
        if (opt.isEmpty){
            // TODO Load from DB
            null
        }else{
            opt.get
        }
    }
    
    /** Returns an immutable copy of the parameter array.
     *
     * @return Immutable copy of the parameter array.
     */
    def parameters = {
        val params = List[Parameter[_]]()
        _parameterIDs foreach { paramID: String =>
            val p: Option[Parameter[_]] = _cachedParameters.get(paramID)
            if (p.isEmpty){
                // TODO loading from DB
            }else{
                p.get :: params
            }
        }
        params.reverse
    }

    /** Removes a parameter from the parameter list.
     *
     * @param p The parameter to be remove.
     *
     * @throws IllegalArgumentException if the plugin's parameter array
     *             doesn't contain this parameter.
     */
    def removeParameter(p: Parameter[_]) = {
        require(containsParameter(p), "Cannot remove a parameter that isn't a member of this plugin!")
        _parameterIDs -= p.id
        _cachedParameters.remove(p.id)
    }

}
