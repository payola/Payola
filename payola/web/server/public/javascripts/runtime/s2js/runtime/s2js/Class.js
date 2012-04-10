goog.provide('s2js.Class');
s2js.Class = function(fullName, parentClassesJsArray) {
var self = this;
self.fullName = fullName;
self.parentClassesJsArray = parentClassesJsArray;
};
s2js.Class.prototype.isSubClassOrEqual = function(classFullName) {
var self = this;

        if (self.fullName === classFullName) {
            return true;
        }
        for (var i in self.parentClassesJsArray) {
            var parentClass = s2js.classOf(self.parentClassesJsArray[i].prototype)
            if (parentClass.isSubClassOrEqual(classFullName)) {
                return true;
            }
        }
        return false;
    };
s2js.Class.prototype.__class__ = new s2js.Class('s2js.Class', []);
