goog.provide('s2js');
goog.require('goog');
goog.require('scala.ClassCastException');
goog.require('scala.NotImplementedException');
s2js.classOf = function(anObject) {
var self = this;

        if (!s2js.isUndefined(anObject.__class__)) {
            return anObject.__class__;
        }
        return null;
    };
s2js.isInstanceOf = function(anObject, classFullName) {
var self = this;
var classNameIsAny = (classFullName == 'Any');
var classNameIsAnyOrAnyVal = (classNameIsAny || (classFullName == 'AnyVal'));
var classNameIsAnyOrAnyRef = (classNameIsAny || (classFullName == 'AnyRef'));
return (function($selector$1) {
if (($selector$1 === 'undefined') || ($selector$1 === 'null')) {
return false;
}
if ($selector$1 === 'number') {
return (function($selector$2) {
if (($selector$2 === 'scala.Byte') || ($selector$2 === 'scala.Short') || ($selector$2 === 'scala.Int') || ($selector$2 === 'scala.Long')) {
return self.isInteger(anObject);
}
if (($selector$2 === 'scala.Float') || ($selector$2 === 'scala.Double')) {
return true;
}
if (true) {
return classNameIsAnyOrAnyVal;
}
})(classFullName);
}
if ($selector$1 === 'boolean') {
return (classNameIsAnyOrAnyVal || (classFullName == 'scala.Boolean'));
}
if ($selector$1 === 'string') {
return (function($selector$3) {
if ($selector$3 === 'scala.Char') {
return self.isChar(anObject);
}
if ($selector$3 === 'scala.String') {
return true;
}
if (true) {
return classNameIsAnyOrAnyRef;
}
})(classFullName);
}
if ($selector$1 === 'function') {
return (function() {
throw new scala.NotImplementedException('Type check of a function isn\'t supported.');
})();
}
if ($selector$1 === 'object') {
if (classNameIsAnyOrAnyRef) {
return true;
}
}
if (true) {
if ((self.classOf(anObject) != null)) {
return self.classOf(anObject).isSubClassOrEqual(classFullName);
}
}
if (true) {
return false;
}
})(goog.typeOf(anObject));

};
s2js.asInstanceOf = function(anObject, className) {
var self = this;
if ((! self.isInstanceOf(anObject, className))) {
(function() {
throw new scala.ClassCastException((((('The object \'' + anObject.toString()) + '\' can\'t be casted to ') + className) + '.'));
})();
}

return anObject;

};
s2js.isUndefined = function(anObject) {
var self = this;
return goog.typeOf(anObject) === 'undefined';};
s2js.isInteger = function(anObject) {
var self = this;
return anObject % 1 === 0;};
s2js.isChar = function(anObject) {
var self = this;
return anObject.length === 1;};
s2js.__class__ = new s2js.Class('s2js', []);
