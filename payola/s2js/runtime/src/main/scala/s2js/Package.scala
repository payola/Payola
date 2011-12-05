package s2js

import s2js.compiler.Native

package object `package` {
    @Native("""
        var jsTypeName = goog.typeOf(comparedObject);
        var typeNameIsPrimitive = typeName === 'AnyVal' || typeName === 'Any';
        var typeNameIsReference = typeName === 'AnyRef' || typeName === 'Any';
        
        // Primitive types doesn't have metaclasses so they're checked immediately.
        if (jsTypeName === 'undefined' || jsTypeName === 'null') {
            return false;
        }
        if (jsTypeName === 'number') {
            if (typeName === 'Byte' || typeName === 'Short' || typeName === 'Integer' || typeName === 'Long') {
                return comparedObject % 1 === 0;
            }
            return typeNameIsPrimitive || typeName === 'Float' || typeName === 'Double';
        }
        if (jsTypeName === 'boolean') {
            return typeNameIsPrimitive || typeName === 'Boolean';
        }
        if (jsTypeName === 'string') {
            if (typeName === 'Char') {
                return comparedObject.length === 1;
            }
            return typeNameIsReference || typeName === 'String';
        }
        if (jsTypeName === 'object' && typeNameIsReference) {
            return true;
        }
        if (jsTypeName === 'function') {
            // TODO
            console.log('Unsupported type check against function type.');
        }

        // Otherwise check whether the object has a metaclass with specified name.
        return s2js.isMetaClassOf(comparedObject.metaClass_, typeName);
    """)
    def isInstanceOf(comparedObject: Any, typeName: String): Boolean = false

    @Native("""
        if (goog.typeOf(metaClass) !== 'object') {
            return false;
        }
        if (metaClass.fullName === className) {
            return true;
        }
        for (var i in metaClass.parentClasses) {
            if (isMetaClassOf(metaClass.parentClasses[i].prototype.metaClass_, typeName)) {
                return true;
            }
        }
        return false;
    """)
    def isMetaClassOf(metaClass: MetaClass, className: String): Boolean = false
}
