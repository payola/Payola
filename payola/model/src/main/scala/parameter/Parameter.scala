package cz.payola.model.parameter

abstract  class Parameter[T](private val n: String, private val defaultValue: T) {
    /** Parameter name. */
    private var _name: String = null
    setName(n)

    def createInstance(value: Option[T]) = {
        if (value.isEmpty)
            instanceWithValue(defaultValue)
        else
            instanceWithValue(value.get)
    }

    /** Returns a new ParameterInstance instance (of its subclass, to be precise) with the value passed
     * as a parameter of this method.
     *
     * @parameter value The value.
     *
     * @return New ParameterInstance instance.
     */
    protected def instanceWithValue(value: T): ParameterInstance[T]

    /** Name getter.
     *
     * @return Name of the parameter.
     */
    def name: String = _name

    /** Name setter.
     *
     * @param newName New name.
     *
     * @throws AssertionError if newName is null or empty.
     */
    def name_=(newName: String) = {
        assert(newName != null && newName != "", "Cannot set null or empty name!")
        _name = newName
    }

    /** Convenience method that just calls name_=()
     *
     * @param newName New name.
     *
     * @throws AssertionError if newName is null or empty.
     */
    def setName(newName: String) = name_=(newName)


}
