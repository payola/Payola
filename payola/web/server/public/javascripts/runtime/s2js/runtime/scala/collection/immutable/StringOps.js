goog.provide('scala.collection.immutable.StringOps');
goog.require('scala.collection.Seq');
goog.require('scala.collection.SeqCompanion');
goog.require('scala.collection.immutable.List');
scala.collection.immutable.StringOps = function(x) {
var self = this;
self.x = x;
goog.base(self);
self.initializeInternalJsArray(self.x);
};
goog.inherits(scala.collection.immutable.StringOps, scala.collection.Seq);
scala.collection.immutable.StringOps.prototype.newInstance = function() {
var self = this;
return scala.collection.immutable.StringOps.empty();
};
scala.collection.immutable.StringOps.prototype.initializeInternalJsArray = function(value) {
var self = this;
self.setInternalJsArray(value.split(''))};
scala.collection.immutable.StringOps.prototype.repr = function() {
var self = this;
return self.getInternalJsArray().join();};
scala.collection.immutable.StringOps.prototype.toBoolean = function() {
var self = this;
return self.x == 'true';};
scala.collection.immutable.StringOps.prototype.toByte = function() {
var self = this;
return parseInt(self.x);};
scala.collection.immutable.StringOps.prototype.toShort = function() {
var self = this;
return parseInt(self.x);};
scala.collection.immutable.StringOps.prototype.toInt = function() {
var self = this;
return parseInt(self.x);};
scala.collection.immutable.StringOps.prototype.toLong = function() {
var self = this;
return parseInt(self.x);};
scala.collection.immutable.StringOps.prototype.toFloat = function() {
var self = this;
return parseFloat(self.x);};
scala.collection.immutable.StringOps.prototype.toDouble = function() {
var self = this;
return parseFloat(self.x);};
scala.collection.immutable.StringOps.prototype.toString = function() {
var self = this;
return self.x;
};
scala.collection.immutable.StringOps.prototype.__class__ = new s2js.Class('scala.collection.immutable.StringOps', [scala.collection.Seq]);
goog.object.extend(scala.collection.immutable.StringOps, new scala.collection.SeqCompanion());
scala.collection.immutable.StringOps.empty = function() {
var self = this;
return new scala.collection.immutable.StringOps('');
};
scala.collection.immutable.StringOps.$apply = function() {
var self = this;
var xs = scala.collection.immutable.List.fromJsArray([].splice.call(arguments, 0, arguments.length - 0));
return self.fromJsArray(xs.getInternalJsArray());};
scala.collection.immutable.StringOps.__class__ = new s2js.Class('scala.collection.immutable.StringOps', [scala.collection.SeqCompanion]);
