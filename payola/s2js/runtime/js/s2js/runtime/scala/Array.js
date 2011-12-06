goog.provide('scala.Array');
scala.Array = function(length) {
var self = this;
self.length = length;
self.internalArray = [];};
scala.Array.prototype.s2js_apply = function(i) {
var self = this;
return self.internalArray[i];};
scala.Array.prototype.clone = function() {
var self = this;
return self.internalArray.slice(0);};
scala.Array.prototype.update = function(i, x) {
var self = this;
self.internalArray[i] = x;};
scala.Array.prototype.metaClass_ = new s2js.MetaClass('scala.Array', []);
if (typeof(scala.Array) === 'undefined') { scala.Array = {}; }
scala.Array.fromNative = function(jsArray) {
var self = this;

        var a = new scala.Array(jsArray.length);
        a.internalArray = jsArray;
        return a;
    };
scala.Array.s2js_apply = function() {
var self = this;
var xs = scala.Array.fromNative([].splice.call(arguments, 0, arguments.length - 0));
var array = new scala.Array(xs.length);
var i = 0;
xs.iterator.foreach(function(x) { array.update(i, x);
i = (i + 1);
 });
return array;
};
scala.Array.metaClass_ = new s2js.MetaClass('scala.Array', []);
