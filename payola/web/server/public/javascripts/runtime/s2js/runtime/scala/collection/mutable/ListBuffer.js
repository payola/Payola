goog.provide('scala.collection.mutable.ListBuffer');
goog.require('scala.collection.Seq');
goog.require('scala.collection.SeqCompanion');
goog.require('scala.collection.immutable.List');
scala.collection.mutable.ListBuffer = function() {
var self = this;
goog.base(self);
};
goog.inherits(scala.collection.mutable.ListBuffer, scala.collection.Seq);
scala.collection.mutable.ListBuffer.prototype.newInstance = function() {
var self = this;
return scala.collection.mutable.ListBuffer.empty();
};
scala.collection.mutable.ListBuffer.prototype.__class__ = new s2js.Class('scala.collection.mutable.ListBuffer', [scala.collection.Seq]);
goog.object.extend(scala.collection.mutable.ListBuffer, new scala.collection.SeqCompanion());
scala.collection.mutable.ListBuffer.empty = function() {
var self = this;
return new scala.collection.mutable.ListBuffer();
};
scala.collection.mutable.ListBuffer.$apply = function() {
var self = this;
var xs = scala.collection.immutable.List.fromJsArray([].splice.call(arguments, 0, arguments.length - 0));
return self.fromJsArray(xs.getInternalJsArray());};
scala.collection.mutable.ListBuffer.__class__ = new s2js.Class('scala.collection.mutable.ListBuffer', [scala.collection.SeqCompanion]);
