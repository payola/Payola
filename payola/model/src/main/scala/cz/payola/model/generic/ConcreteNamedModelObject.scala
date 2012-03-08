package cz.payola.model.generic

import cz.payola.common.model.NamedModelObject

class ConcreteNamedModelObject(var n: String) extends ConcreteModelObject with NamedModelObject
{
    protected var _name: String = null

    setName(n)

    /** Returns the object's name.
      *
      * @return Object's name.
      */
    def name: String = _name

    /** Sets the object's name.
      *
      * @param n New name.
      *
      * @throws IllegalArgumentException if the new name is null or empty.
      */
    def name_=(n: String) = {
        // The name mustn't be null and mustn't be empty
        require(n != null && n != "")

        _name = n
    }

    /** Convenience method that just calls name_=.
      *
      * @param n The new object's name.
      *
      * @throws IllegalArgumentException if the new name is null or empty.
      */
    def setName(n: String) = name_=(n);
}
