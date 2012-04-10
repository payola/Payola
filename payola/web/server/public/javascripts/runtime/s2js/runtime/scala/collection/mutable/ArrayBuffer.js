goog.provide('scala.collection.mutable.ArrayBuffer');
goog.require('scala.collection.Seq');
goog.require('scala.collection.SeqCompanion');
goog.require('scala.collection.immutable.List');
scala.collection.mutable.ArrayBuffer = function() {
var self = this;
goog.base(self);
};
goog.inherits(scala.collection.mutable.ArrayBuffer, scala.collection.Seq);
scala.collection.mutable.ArrayBuffer.prototype.newInstance = function() {
var self = this;
return scala.collection.mutable.ArrayBuffer.empty();
};
scala.collection.mutable.ArrayBuffer.prototype.__class__ = new s2js.Class('scala.collection.mutable.ArrayBuffer', [scala.collection.Seq]);
goog.object.extend(scala.collection.mutable.ArrayBuffer, new scala.collection.SeqCompanion());
scala.collection.mutable.ArrayBuffer.empty = function() {
var self = this;
return new scala.collection.mutable.ArrayBuffer();
};
scala.collection.mutable.ArrayBuffer.$apply = function() {
var self = this;
var xs = scala.collection.immutable.List.fromJsArray([].splice.call(arguments, 0, arguments.length - 0));
return self.fromJsArray(xs.getInternalJsArray());};
scala.collection.mutable.ArrayBuffer.__class__ = new s2js.Class('scala.collection.mutable.ArrayBuffer', [scala.collection.SeqCompanion]);
