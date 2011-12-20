goog.provide('scala.Array');
scala.Array = function(length) {
var self = this;
self.length = length;
self.internalArray = [];
};
scala.Array.prototype.$apply = function(i) {
var self = this;
return self.internalArray[i];};
scala.Array.prototype.clone = function() {
var self = this;
return self.internalArray.slice(0);};
scala.Array.prototype.update = function(i, x) {
var self = this;
self.internalArray[i] = x;};
scala.Array.prototype.foreach = function(f) {
var self = this;
self.internalArray.forEach(f);};
scala.Array.prototype.metaClass_ = new s2js.MetaClass('scala.Array', []);
scala.Array.fromNative = function(nativeArray) {
var self = this;

        var a = new scala.Array(nativeArray.length);
        a.internalArray = nativeArray;
        return a;
    };
scala.Array.$apply = function() {
var self = this;
var xs = scala.Array.fromNative([].splice.call(arguments, 0, arguments.length - 0));
return self.fromNative(xs.internalArray);};
scala.Array.metaClass_ = new s2js.MetaClass('scala.Array', []);
