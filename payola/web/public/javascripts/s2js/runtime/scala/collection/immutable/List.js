goog.provide('scala.collection.immutable.List');
goog.require('scala.Array');
goog.require('scala.Option');
scala.collection.immutable.List = function() {
var self = this;
self.internalArray = [];
};
scala.collection.immutable.List.prototype.foreach = function(f) {
var self = this;
for (var i in self.internalArray) f(self.internalArray[i]);}
;
scala.collection.immutable.List.prototype.exists = function(p) {
var self = this;
return self.find(p).isDefined();
}
;
scala.collection.immutable.List.prototype.find = function(p) {
var self = this;
return scala.Option.$apply(self.nativeFind(p));
}
;
scala.collection.immutable.List.prototype.nativeFind = function(p) {
var self = this;

        for (var i in self.internalArray) {
            if (p(self.internalArray[i])) {
                return self.internalArray[i];
            }
        }
        return null;
    }
;
scala.collection.immutable.List.prototype.metaClass_ = new s2js.MetaClass('scala.collection.immutable.List', []);
scala.collection.immutable.List.fromNative = function(nativeArray) {
var self = this;

        var a = new scala.collection.immutable.List();
        a.internalArray = nativeArray;
        return a;
    }
;
scala.collection.immutable.List.$apply = function() {
var self = this;
var xs = scala.Array.fromNative([].splice.call(arguments, 0, arguments.length - 0));
return self.fromNative(xs.internalArray);}
;
scala.collection.immutable.List.metaClass_ = new s2js.MetaClass('scala.collection.immutable.List', []);
