goog.provide('scala.collection.Seq');
goog.require('scala.collection.Iterable');
goog.require('scala.util.control.Breaks');
scala.collection.Seq = function() {
var self = this;
self.internalJsArray = [];
};
goog.inherits(scala.collection.Seq, scala.collection.Iterable);
scala.collection.Seq.prototype.getInternalJsArray = function() {
var self = this;
return self.internalJsArray;
};
scala.collection.Seq.prototype.setInternalJsArray = function(value) {
var self = this;
self.internalJsArray = value;
};
scala.collection.Seq.prototype.foreach = function(f) {
var self = this;

        for (var i in self.getInternalJsArray()) {
            f(self.getInternalJsArray()[i]);
        }
    };
scala.collection.Seq.prototype.$plus$eq = function(x) {
var self = this;
self.getInternalJsArray().push(x);};
scala.collection.Seq.prototype.reversed = function() {
var self = this;
var elems = self.newInstance();
self.foreach(function(x) {
elems.prepend(x);
});
return elems;

};
scala.collection.Seq.prototype.size = function() {
var self = this;
return self.getInternalJsArray().length;};
scala.collection.Seq.prototype.$apply = function(n) {
var self = this;

        if (s2js.isUndefined(self.getInternalJsArray()[n])) {
            throw new scala.NoSuchElementException('An item with index ' + n + ' is not present.');
        }
        return self.getInternalJsArray()[n];
    };
scala.collection.Seq.prototype.update = function(n, newelem) {
var self = this;

        if (self.size() <= n) {
            throw new scala.NoSuchElementException('An item with index ' + n + ' is not present.');
        }
        self.getInternalJsArray()[n] = newelem;
    };
scala.collection.Seq.prototype.$length = function() {
var self = this;
return self.size();
};
scala.collection.Seq.prototype.remove = function(index) {
var self = this;

        if (index < 0 || self.size() <= index) {
            throw new scala.NoSuchElementException('An item with index ' + n + ' is not present.');
        }
        var removed = self.getInternalJsArray()[index];
        self.getInternalJsArray().splice(index, 1);
        return removed;
    };
scala.collection.Seq.prototype.prepend = function(x) {
var self = this;
self.getInternalJsArray().splice(0, 0, x);};
scala.collection.Seq.prototype.$minus$eq = function(x) {
var self = this;

        var index = self.getInternalJsArray().indexOf(x);
        if (index != -1) {
            self.getInternalJsArray().splice(index, 1);
        }
    };
scala.collection.Seq.prototype.indexWhere = function(p, from) {
var self = this;
if (typeof(from) === 'undefined') { from = 0; }
var i = from;
scala.util.control.Breaks.breakable(function() {
self.drop(from).foreach(function(x) {
if (p(x)) {
scala.util.control.Breaks.$break();
} else {
i = (i + 1);
}

});
i = -1;

});
return i;

};
scala.collection.Seq.prototype.diff = function(that) {
var self = this;

        return self.getInternalJsArray().filter(function(i) {
            return !(that.getInternalJsArray().indexOf(i) > -1);
        });
        };
scala.collection.Seq.prototype.__class__ = new s2js.Class('scala.collection.Seq', [scala.collection.Iterable]);
