package cz.payola.common.model


trait Parameter[A] extends NamedModelObject {

    /** Returns a new ParameterInstance instance (of its subclass, to be precise) with the value passed
      * as a parameter of this method.
      *
      * @param value The value.
      *
      * @return New ParameterInstance instance.
      */
    protected def instanceWithValue(value: A): ParameterInstance[A]

}
