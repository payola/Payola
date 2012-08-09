package s2js.runtime.client.core

import s2js.adapters.js

/**
 * A class representing a class.
 * @param fullName Fully qualified name of the class.
 * @param parentClasses The parent classes.
 */
class Class(val fullName: String, val parentClasses: js.Array[String])
{
    def getName: String = fullName
}
