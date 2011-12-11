package cz.payola.model

import cz.payola.model.parameter._
import collection.mutable.ArrayBuffer

class Plugin (val n: String, val params: ArrayBuffer[Parameter[_]] = new ArrayBuffer[Parameter[_]]()) {
    // Parameters. Doesn't need a setter as all we need to check is that it's not null
    private val _parameters: ArrayBuffer[Parameter[_]] = params
    require(params != null, "Cannot pass null params array to Plugin's constructor!")

    // Plugin's name. Mustn't be null or empty
    private var _name: String = null
    setName(n)

    /** Adds a new parameter to the parameter list.
     *
     * @param p Parameter to be added?
     *
     * @throws IllegalArgumentException if the parameter is null.
     */
    def addParameter(p: Parameter[_]) = {
        require(p != null, "Cannot add null parameter!")
        if (!containsParameter(p))
            _parameters += p
    }

    /** Checks whether the parameter is in the parameter list or not.
     *
     * @param p The parameter.
     *
     * @return True or false.
     */
    def containsParameter(p: Parameter[_]): Boolean = _parameters.contains(p)

    /** Returns the plugin's name.
     *
     * @return Plugin's name.
     */
    def name: String = _name

    /** Sets the plugin name.
     *
     * @param newName New name.
     *
     * @throws IllegalArgumentException if the new name is null or empty.
     */
    def name_=(newName: String) = {
        require(newName != null, "Cannot set plugin's name to null!")
        require(newName != "", "Cannot set plugin's name to empty string!")

        _name = newName
    }

    /** Returns an immutable copy of the parameter array.
     *
     * @return Immutable copy of the parameter array.
     */
    def parameters: Array[Parameter[_]] = params.toArray

    /** Removes a parameter from the parameter list.
     *
     * @param p The parameter to be remove.
     *
     * @throws IllegalArgumentException if the plugin's parameter array
     *             doesn't contain this parameter.
     */
    def removeParameter(p: Parameter[_]) = {
        require(_parameters.contains(p), "Cannot remove a parameter that isn't a member of this plugin!")
        _parameters -= p
    }

    /** Convenience method that just calls name_=.
     *
     * @param n The new user's name.
     *
     * @throws IllegalArgumentException if the new name is null or empty.
     */
    def setName(newName: String) = name_=(newName)

}
