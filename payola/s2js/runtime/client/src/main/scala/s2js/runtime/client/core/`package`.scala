package s2js.runtime.client.core

import s2js.adapters.js
import s2js.compiler.javascript

object `package`
{
    val classLoader = new ClassLoader

    /**
     * Returns whether the specified object is the undefined.
     */
    @javascript("return typeof obj === 'undefined';")
    def isUndefined(obj: Any): Boolean = false

    /**
     * Returns whether the specified object is not the undefined.
     */
    def isDefined(obj: Any): Boolean = !isUndefined(obj)

    /**
     * Returns whether the specified object is a JavaScript object.
     */
    @javascript("return obj === Object(obj);")
    def isObject(obj: Any): Boolean = false

    /**
     * Returns whether the specified object is a JavaScript array.
     */
    def isArray(obj: Any): Boolean = nativeToString(obj) == "[object Array]"

    /**
     * Returns whether the specified object is a JavaScript function.
     */
    def isFunction(obj: Any): Boolean = nativeToString(obj) == "[object Function]"

    /**
     * Returns whether the specified object is a JavaScript string.
     */
    def isString(obj: Any): Boolean = nativeToString(obj) == "[object String]"

    /**
     * Returns whether the specified object is a JavaScript number.
     */
    def isNumber(obj: Any): Boolean = nativeToString(obj) == "[object Number]"

    /**
     * Returns whether the specified object is a JavaScript boolean.
     */
    @javascript("return obj === true || obj === false || self.nativeToString(obj) == '[object Boolean]';")
    def isBoolean(obj: Any): Boolean = false

    /**
     * Returns whether the specified object can be perceived as an integer.
     */
    @javascript("return self.isNumber(obj) && (obj % 1 === 0);")
    def isInteger(obj: Any): Boolean = false

    /**
     * Returns whether the specified object can be perceived as one character.
     */
    @javascript("return self.isString(obj) && obj.length === 1;")
    def isChar(obj: Any): Boolean = false

    /**
     * Initializes the inheritance relation between the specified sub class and super class.
     * @param subClass A class that should inherit from the super class.
     * @param superClass A class from which the sub class inherits.
     */
    @javascript(
        """
            function temporaryConstructor() {};
            temporaryConstructor.prototype = superClass.prototype;
            subClass.prototype = new temporaryConstructor();
            subClass.prototype.constructor = subClass;
        """)
    def inherit(subClass: Any, superClass: Any) {}

    /**
     * Copies all the fields from the source object to the target object. Copies only those fields that aren't directly
     * defined in the target object.
     * @param targetObject An object where the values are copied to.
     * @param sourceObject An object whose values are copied to the target object.
     */
    @javascript(
        """
            for (var i in sourceObject) {
                if (!self.isFunction(sourceObject[i]) &&
                    !Object.prototype.hasOwnProperty.call(targetObject, i) &&
                    !Object.prototype.hasOwnProperty.call(targetObject.constructor.prototype, i)) {
                    targetObject[i] = sourceObject[i];
                }
            }
        """)
    def mixInFields(targetObject: Any, sourceObject: Any) {}

    /**
     * Copies all the functions from the source object to the target object. Copies only those functions that aren't
     * directly defined in the target object prototype.
     * @param targetObject An object where the values are copied to.
     * @param sourceObject An object whose values are copied to the target object.
     */
    @javascript(
        """
            for (var i in sourceObject) {
                if (self.isFunction(sourceObject[i]) &&
                    !Object.prototype.hasOwnProperty.call(targetObject.constructor.prototype, i)) {
                    targetObject[i] = sourceObject[i];
                }
            }
        """)
    def mixInFunctions(targetObject: Any, sourceObject: Any) {}

    /**
     * Copies all the values (fields and functions) from the source object to the target object.
     * @param targetObject An object where the values are copied to.
     * @param sourceObject An object whose values are copied to the target object.
     */
    def mixIn(targetObject: Any, sourceObject: Any) {
        mixInFields(targetObject, sourceObject)
        mixInFunctions(targetObject, sourceObject)
    }

    /**
     * Returns whether the specified path correspond to an existing object (i.e. whether the 'evaled' path is defined).
     */
    def isObjectDefined(path: String): Boolean = isDefined(js.eval(path))

    /**
     * Declares all objects on the specified object path in case they're not already defined.
     */
    @javascript(
        """
            var parentPath = window;
            while (path != '') {
                var index = path.indexOf('.');
                var name = '';
                if (index >= 0) {
                    name = path.substring(0, index);
                    path = path.substring(index + 1);
                } else {
                    name = path;
                    path = '';
                }

                if (self.isUndefined(parentPath[name])) {
                    parentPath[name] = {};
                }
                parentPath = parentPath[name];
            }
        """)
    def declareObject(path: String) {}

    /**
     * Returns [[s2js.runtime.client.core.Class]] corresponding to the specified object. If the object doesn't have any
     * class specified, returns null.
     */
    @javascript(
        """
            if (self.isDefined(obj.__class__)) {
                return obj.__class__;
            }
            return null;
        """)
    def classOf(obj: Any): Class = null

    /**
     * Returns whether the specified object is an instance of class with the specified name.
     */
    private def isInstanceOf(obj: Any, className: String): Boolean = {
        val classNameIsAny = className == "scala.Any"
        val classNameIsAnyOrAnyVal = classNameIsAny || className == "scala.AnyVal"
        val classNameIsAnyOrAnyRef = classNameIsAny || className == "scala.AnyRef"

        if (isUndefined(obj) || obj == null) {
            false
        } else if (isNumber(obj)) {
            className match {
                case "scala.Byte" | "scala.Short" | "scala.Int" | "scala.Long" => isInteger(obj)
                case "scala.Float" | "scala.Double" => true
                case _ => classNameIsAnyOrAnyVal
            }
        } else if (isBoolean(obj)) {
            classNameIsAnyOrAnyVal || className == "scala.Boolean"
        } else if (isString(obj)) {
            className match {
                case "scala.Char" => isChar(obj)
                case "scala.String" => true
                case _ => classNameIsAnyOrAnyRef
            }
        } else if (isObject(obj) && classNameIsAnyOrAnyRef) {
            true
        } else {
            val objClass = classOf(obj)
            objClass != null && classIsSubClassOrEqual(objClass, className)
        }
    }

    @javascript(
        """
            if (c.fullName === classFullName) {
                return true;
            }
            for (var i in c.parentClasses) {
                var parentClass = self.classOf(c.parentClasses[i].prototype)
                if (parentClass !== null && self.classIsSubClassOrEqual(parentClass, classFullName)) {
                    return true;
                }
            }
            return false;
        """)
    private def classIsSubClassOrEqual(c: Class, classFullName: String): Boolean = false

    /**
     * Casts the specified object to the specified type. Just checks whether the conversion is possible. Nothing has to
     * be done with the object.
     */
    private def asInstanceOf(obj: Any, className: String): Any = {
        if (!isInstanceOf(obj, className)) {
            throw new ClassCastException("The object '" + obj.toString + "' can't be casted to " + className + "")
        }
        obj
    }

    /**
     * Returns native JavaScript string representation of the specified object.
     */
    @javascript("return Object.prototype.toString.call(obj)")
    private def nativeToString(obj: Any): String = ""
}

