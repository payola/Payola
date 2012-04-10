goog.provide('scala.collection.immutable.HashMap');
goog.require('scala.collection.Map');
goog.require('scala.collection.MapCompanion');
scala.collection.immutable.HashMap = function() {
var self = this;
goog.base(self);
};
goog.inherits(scala.collection.immutable.HashMap, scala.collection.Map);
scala.collection.immutable.HashMap.prototype.newInstance = function() {
var self = this;
return scala.collection.immutable.HashMap.empty();
};
scala.collection.immutable.HashMap.prototype.__class__ = new s2js.Class('scala.collection.immutable.HashMap', [scala.collection.Map]);
goog.object.extend(scala.collection.immutable.HashMap, new scala.collection.MapCompanion());
scala.collection.immutable.HashMap.empty = function() {
var self = this;
return new scala.collection.immutable.HashMap();
};
scala.collection.immutable.HashMap.__class__ = new s2js.Class('scala.collection.immutable.HashMap', [scala.collection.MapCompanion]);
