goog.provide('scala.collection.MapCompanion');
goog.require('scala.collection.immutable.List');
scala.collection.MapCompanion = function() {
var self = this;
};
scala.collection.MapCompanion.prototype.$apply = function() {
var self = this;
var xs = scala.collection.immutable.List.fromJsArray([].splice.call(arguments, 0, arguments.length - 0));
var m = self.empty();
xs.foreach(function($x$1) {
m.$plus$eq($x$1);
});
return m;

};
scala.collection.MapCompanion.prototype.__class__ = new s2js.Class('scala.collection.MapCompanion', []);
