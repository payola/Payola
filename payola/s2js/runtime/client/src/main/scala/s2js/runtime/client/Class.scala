package s2js.runtime.client

import s2js.compiler.javascript

/**
  * A class representing a Class.
  * @param fullName Fully qualified name of the class.
  * @param parentClassesJsArray A JavaScript array of the parent classes. It has to be done that way because in the time
  *     Class is instantiated, there are no scala collections available.
  */
class Class(val fullName: String, val parentClassesJsArray: Any)
{
    @javascript("""
        if (self.fullName === classFullName) {
            return true;
        }
        for (var i in self.parentClassesJsArray) {
            var parentClass = s2js.runtime.client.classOf(self.parentClassesJsArray[i].prototype)
            if (parentClass.isSubClassOrEqual(classFullName)) {
                return true;
            }
        }
        return false;
    """)
    def isSubClassOrEqual(classFullName: String): Boolean = false
}
