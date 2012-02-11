goog.provide('s2js.MetaClass');
s2js.MetaClass = function(fullName, parentClasses) {
var self = this;
self.fullName = fullName;
self.parentClasses = parentClasses;
};
s2js.MetaClass.prototype.metaClass_ = new s2js.MetaClass('s2js.MetaClass', []);
