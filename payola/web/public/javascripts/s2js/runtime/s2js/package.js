goog.provide('s2js');
goog.require('goog');
goog.require('scala.RuntimeException');
s2js.isInstanceOf = function(anObject, className) {
var self = this;
var classNameIsAny = (className == 'Any');
var classNameIsAnyOrVal = (classNameIsAny || (className == 'AnyVal'));
var classNameIsAnyOrRef = (classNameIsAny || (className == 'AnyRef'));
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
return classNameIsAnyOrVal;
}
})(className);
}
if ($selector$1 === 'boolean') {
return (classNameIsAnyOrVal || (className == 'scala.Boolean'));
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
return classNameIsAnyOrRef;
}
})(className);
}
if ($selector$1 === 'object') {
if (classNameIsAnyOrRef) {
return true;
}
}
if ($selector$1 === 'function') {
return false;
}
if (true) {
return self.isInMetaClassHierarchy(self.getObjectMetaClass(anObject), className);
}
})(goog.typeOf(anObject));

};
s2js.isInMetaClassHierarchy = function(rootMetaClass, metaClassName) {
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
return self.existsParentMetaClass(rootMetaClass, function(pmc) {
return self.isInMetaClassHierarchy(pmc, metaClassName);
});
}})();
}})();
};
s2js.isInteger = function(anObject) {
var self = this;
return anObject % 1 === 0;};
s2js.isChar = function(anObject) {
var self = this;
return anObject.length === 1;};
s2js.getObjectMetaClass = function(anObject) {
var self = this;
return anObject.metaClass_;};
s2js.existsParentMetaClass = function(rootMetaClass, predicate) {
var self = this;

        for (var i in rootMetaClass.parentClasses) {
            if (predicate(self.getMetaClass(rootMetaClass.parentClasses[i].prototype))) {
                return true;
            }
        }
        return false;
    };
s2js.asInstanceOf = function(anObject, className) {
var self = this;
if ((! self.isInstanceOf(anObject, className))) {
(function() {
throw new scala.RuntimeException();
})();
}
return anObject;

};
s2js.metaClass_ = new s2js.MetaClass('s2js', []);
