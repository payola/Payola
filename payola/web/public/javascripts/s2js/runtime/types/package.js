goog.provide('types');
goog.require('goog');
goog.require('scala.RuntimeException');
types.isInstanceOf = function(anObject, className) {
var self = this;
var classNameIsAny = (className == 'Any');
var classNameIsAnyOrVal = (classNameIsAny || (className == 'AnyVal'));
var classNameIsAnyOrRef = (classNameIsAny || (className == 'AnyRef'));
return (function($selector_1) {
if (($selector_1 === 'undefined') || ($selector_1 === 'null')) {
return false;
}
if ($selector_1 === 'number') {
return (function($selector_2) {
if (($selector_2 === 'scala.Byte') || ($selector_2 === 'scala.Short') || ($selector_2 === 'scala.Int') || ($selector_2 === 'scala.Long')) {
return self.isInteger(anObject);
}
if (($selector_2 === 'scala.Float') || ($selector_2 === 'scala.Double')) {
return true;
}
if (true) {
return classNameIsAnyOrVal;
}
})(className);
}
if ($selector_1 === 'boolean') {
return (classNameIsAnyOrVal || (className == 'scala.Boolean'));
}
if ($selector_1 === 'string') {
return (function($selector_3) {
if ($selector_3 === 'scala.Char') {
return self.isChar(anObject);
}
if ($selector_3 === 'scala.String') {
return true;
}
if (true) {
return classNameIsAnyOrRef;
}
})(className);
}
if ($selector_1 === 'object') {
if (classNameIsAnyOrRef) {
return true;
}
}
if ($selector_1 === 'function') {
return false;
}
if (true) {
return self.isInMetaClassHierarchy(self.getObjectMetaClass(anObject), className);
}
})(goog.typeOf(anObject));
};
types.isInMetaClassHierarchy = function(rootMetaClass, metaClassName) {
var self = this;
return (function() {
if ((goog.typeOf(rootMetaClass) != 'object')) {
return (function() {
throw new scala.RuntimeException();
})();
} else {
return (function() {
if ((metaClassName == rootMetaClass.fullName)) {
return true;
} else {
return self.existsParentMetaClass(rootMetaClass, function(pmc) { return self.isInMetaClassHierarchy(pmc, metaClassName);
 });
}})();
}})();
};
types.isInteger = function(anObject) {
var self = this;
return anObject % 1 === 0;};
types.isChar = function(anObject) {
var self = this;
return anObject.length === 1;};
types.getObjectMetaClass = function(anObject) {
var self = this;
return anObject.metaClass_;};
types.existsParentMetaClass = function(rootMetaClass, predicate) {
var self = this;

        for (var i in rootMetaClass.parentClasses) {
            if (predicate(self.getMetaClass(rootMetaClass.parentClasses[i].prototype)) {
                return true;
            }
        }
        return false;
    };
types.asInstanceOf = function(anObject, className) {
var self = this;
(function() {
if ((! self.isInstanceOf(anObject, className))) {
(function() {
throw new scala.RuntimeException();
})();
} else {
}})();
return anObject;
};
types.metaClass_ = new types.MetaClass('types', []);
