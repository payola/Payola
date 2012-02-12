goog.provide('scala.collection.mutable.ListBuffer');
goog.require('scala.Array');
goog.require('scala.Option');
scala.collection.mutable.ListBuffer = function() {
var self = this;
self.internalArray = [];
};
scala.collection.mutable.ListBuffer.prototype.foreach = function(f) {
var self = this;
for (var i in self.internalArray) f(self.internalArray[i]);};
scala.collection.mutable.ListBuffer.prototype.exists = function(p) {
var self = this;
return self.find(p).isDefined();
};
scala.collection.mutable.ListBuffer.prototype.find = function(p) {
var self = this;
return scala.Option.$apply(self.nativeFind(p));
};
scala.collection.mutable.ListBuffer.prototype.$plus$eq = function(value) {
var self = this;
self.internalArray.push(value);};
scala.collection.mutable.ListBuffer.prototype.$minus$eq = function(value) {
var self = this;

        var index = internalArray.indexOf(value);
        if (index != -1) {
            internalArray.splice(index, 1);
        }
    };
scala.collection.mutable.ListBuffer.prototype.nativeFind = function(p) {
var self = this;

        for (var i in self.internalArray) {
            if (p(self.internalArray[i])) {
                return self.internalArray[i];
            }
        }
        return null;
    };
scala.collection.mutable.ListBuffer.prototype.metaClass_ = new s2js.MetaClass('scala.collection.mutable.ListBuffer', []);
scala.collection.mutable.ListBuffer.fromNative = function(nativeArray) {
var self = this;

        var a = new scala.collection.mutable.ListBuffer();
        a.internalArray = nativeArray;
        return a;
    };
scala.collection.mutable.ListBuffer.$apply = function() {
var self = this;
var xs = scala.Array.fromNative([].splice.call(arguments, 0, arguments.length - 0));
return self.fromNative(xs.internalArray);};
scala.collection.mutable.ListBuffer.metaClass_ = new s2js.MetaClass('scala.collection.mutable.ListBuffer', []);
