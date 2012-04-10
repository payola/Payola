goog.provide('scala.Predef');
goog.require('scala.collection.immutable.StringOps');
scala.Predef.augmentString = function(x) {
var self = this;
return new scala.collection.immutable.StringOps(x);
};
scala.Predef.unaugmentString = function(x) {
var self = this;
return x.repr();
};
scala.Predef.__class__ = new s2js.Class('scala.Predef', []);
