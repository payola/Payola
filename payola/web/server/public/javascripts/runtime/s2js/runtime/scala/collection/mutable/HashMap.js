goog.provide('scala.collection.mutable.HashMap');
goog.require('scala.collection.Map');
goog.require('scala.collection.MapCompanion');
scala.collection.mutable.HashMap = function() {
var self = this;
goog.base(self);
};
goog.inherits(scala.collection.mutable.HashMap, scala.collection.Map);
scala.collection.mutable.HashMap.prototype.newInstance = function() {
var self = this;
return scala.collection.mutable.HashMap.empty();
};
scala.collection.mutable.HashMap.prototype.__class__ = new s2js.Class('scala.collection.mutable.HashMap', [scala.collection.Map]);
goog.object.extend(scala.collection.mutable.HashMap, new scala.collection.MapCompanion());
scala.collection.mutable.HashMap.empty = function() {
var self = this;
return new scala.collection.mutable.HashMap();
};
scala.collection.mutable.HashMap.__class__ = new s2js.Class('scala.collection.mutable.HashMap', [scala.collection.MapCompanion]);
