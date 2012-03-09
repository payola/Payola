package cz.payola.model

import cz.payola._
import cz.payola.model.parameter._
import collection.mutable.{HashMap, ArrayBuffer}

class Plugin(n: String) extends common.model.Plugin with generic.ConcreteNamedModelObject {
    // Parameters. Doesn't need a setter as all we need to check is that it's not null
    private val _parameterIDs: ArrayBuffer[String] = new ArrayBuffer[String]()
    private val _cachedParameters: HashMap[String, common.model.Parameter[_]] = new HashMap[String, common.model.Parameter[_]]()

    setName(n)

    /** Adds a new parameter to the parameter list.
     *
     * @param p Parameter to be added?
     *
     * @throws IllegalArgumentException if the parameter is null.
     */
    def addParameter(p: common.model.Parameter[_]) = {
        require(p != null, "Cannot add null parameter!")
        if (!containsParameter(p)){
            _parameterIDs += p.objectID
            _cachedParameters.put(p.objectID, p)
        }
    }

    /** Checks whether the parameter is in the parameter list or not.
     *
     * @param p The parameter.
     *
     * @return True or false.
     */
    def containsParameter(p: common.model.Parameter[_]): Boolean = _parameterIDs.contains(p.objectID)

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
    def parameterAtIndex(index: Int): common.model.Parameter[_] = {
        require(index >= 0 && index < numberOfParameters, "Parameter index out of bounds - " + index)
        val opt: Option[common.model.Parameter[_]] = _cachedParameters.get(_parameterIDs(index))
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
    def parameters: List[common.model.Parameter[_]] = {
        val params = List[common.model.Parameter[_]]()
        _parameterIDs foreach { paramID: String =>
            val p: Option[common.model.Parameter[_]] = _cachedParameters.get(paramID)
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
    def removeParameter(p: common.model.Parameter[_]) = {
        require(containsParameter(p), "Cannot remove a parameter that isn't a member of this plugin!")
        _parameterIDs -= p.objectID
        _cachedParameters.remove(p.objectID)
    }

}
