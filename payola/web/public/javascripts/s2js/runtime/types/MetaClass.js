goog.provide('types.MetaClass');
types.MetaClass = function(fullName, parentClasses) {
var self = this;
self.fullName = fullName;
self.parentClasses = parentClasses;
};
types.MetaClass.prototype.metaClass_ = new types.MetaClass('types.MetaClass', []);
