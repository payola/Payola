goog.provide('scala.collection.immutable.List');
goog.provide('scala.collection.immutable.Nil');
goog.require('scala.NoSuchElementException');
goog.require('scala.UnsupportedOperationException');
goog.require('scala.collection.Seq');
goog.require('scala.collection.SeqCompanion');
scala.collection.immutable.List = function() {
var self = this;
goog.base(self);
};
goog.inherits(scala.collection.immutable.List, scala.collection.Seq);
scala.collection.immutable.List.prototype.newInstance = function() {
var self = this;
return scala.collection.immutable.List.empty();
};
scala.collection.immutable.List.prototype.__class__ = new s2js.Class('scala.collection.immutable.List', [scala.collection.Seq]);
goog.object.extend(scala.collection.immutable.List, new scala.collection.SeqCompanion());
scala.collection.immutable.List.empty = function() {
var self = this;
return new scala.collection.immutable.List();
};
scala.collection.immutable.List.$apply = function() {
var self = this;
var xs = scala.collection.immutable.List.fromJsArray([].splice.call(arguments, 0, arguments.length - 0));
return self.fromJsArray(xs.getInternalJsArray());};
scala.collection.immutable.List.__class__ = new s2js.Class('scala.collection.immutable.List', [scala.collection.SeqCompanion]);
goog.object.extend(scala.collection.immutable.Nil, new scala.collection.immutable.List());
scala.collection.immutable.Nil.isEmpty = function() {
var self = this;
return true;
};
scala.collection.immutable.Nil.head = function() {
var self = this;
return (function() {
throw new scala.NoSuchElementException('head of empty list');
})();
};
scala.collection.immutable.Nil.tail = function() {
var self = this;
return (function() {
throw new scala.UnsupportedOperationException('tail of empty list');
})();
};
scala.collection.immutable.Nil.__class__ = new s2js.Class('scala.collection.immutable.Nil', [scala.collection.immutable.List]);
