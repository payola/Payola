goog.provide('types');
goog.require('goog');
goog.require('scala.RuntimeException');
if (typeof(types) === 'undefined') { types = {}; }
types.isInstanceOf = function(anObject, className) {
var self = this;
var jsType = goog.typeOf(anObject);
var classNameIsAny = (className == 'Any');
var classNameIsAnyOrVal = (classNameIsAny || (className == 'AnyVal'));
var classNameIsAnyOrRef = (classNameIsAny || (className == 'AnyRef'));
return matching!!!!!! ;
};
types.isInMetaClassHierarchy = function(rootMetaClass, metaClassName) {
var self = this;
return (function() {
if ((goog.typeOf(rootMetaClass) != 'object')) {
return throw new scala.RuntimeException();
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
throw new scala.RuntimeException();
} else {
}})();
return anObject;
};
types.metaClass_ = new s2js.MetaClass('types', []);
