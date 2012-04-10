goog.provide('scala.collection.Iterable');
goog.require('s2js');
goog.require('scala.NoSuchElementException');
goog.require('scala.None');
goog.require('scala.NotImplementedException');
goog.require('scala.Some');
goog.require('scala.Tuple2');
goog.require('scala.UnsupportedOperationException');
goog.require('scala.math');
goog.require('scala.util.control.Breaks');
scala.collection.Iterable = function() {
var self = this;
};
scala.collection.Iterable.prototype.$plus$plus$eq = function(coll) {
var self = this;
coll.foreach(function($x$1) {
self.$plus$eq($x$1);
});
};
scala.collection.Iterable.prototype.size = function() {
var self = this;
var result = 0;
self.foreach(function(x) {
result = (result + 1);
});
return result;

};
scala.collection.Iterable.prototype.nonEmpty = function() {
var self = this;
return (! self.isEmpty());
};
scala.collection.Iterable.prototype.count = function(p) {
var self = this;
var cnt = 0;
self.foreach(function(x) {
if (p(x)) {
cnt = (cnt + 1);
}

});
return cnt;

};
scala.collection.Iterable.prototype.$div$colon = function(z, op) {
var self = this;
return self.foldLeft(z, op);
};
scala.collection.Iterable.prototype.$colon$bslash = function(z, op) {
var self = this;
return self.foldRight(z, op);
};
scala.collection.Iterable.prototype.foldLeft = function(z, op) {
var self = this;
var result = z;
self.foreach(function(x) {
result = op(result, x);
});
return result;

};
scala.collection.Iterable.prototype.foldRight = function(z, op) {
var self = this;
return self.reversed().foldLeft(z, function(x, y) {
return op(y, x);
});
};
scala.collection.Iterable.prototype.reduceLeft = function(op) {
var self = this;
if (self.isEmpty()) {
(function() {
throw new scala.UnsupportedOperationException('empty.reduceLeft');
})();
}

var first = true;
var acc = 0.0;
self.foreach(function(x) {
if (first) {
acc = x;
first = false;

} else {
acc = op(acc, x);
}

});
return acc;

};
scala.collection.Iterable.prototype.reduceRight = function(op) {
var self = this;
if (self.isEmpty()) {
(function() {
throw new scala.UnsupportedOperationException('empty.reduceRight');
})();
}

return self.reversed().reduceLeft(function(x, y) {
return op(y, x);
});

};
scala.collection.Iterable.prototype.reduceLeftOption = function(op) {
var self = this;
return (function() {
if (self.isEmpty()) {
return scala.None;
} else {
return new scala.Some(self.reduceLeft(op));
}
})();
};
scala.collection.Iterable.prototype.reduceRightOption = function(op) {
var self = this;
return (function() {
if (self.isEmpty()) {
return scala.None;
} else {
return new scala.Some(self.reduceRight(op));
}
})();
};
scala.collection.Iterable.prototype.reduce = function(op) {
var self = this;
return self.reduceLeft(op);
};
scala.collection.Iterable.prototype.reduceOption = function(op) {
var self = this;
return self.reduceLeftOption(op);
};
scala.collection.Iterable.prototype.fold = function(z, op) {
var self = this;
return self.foldLeft(z, op);
};
scala.collection.Iterable.prototype.aggregate = function(z, seqop, combop) {
var self = this;
return self.foldLeft(z, seqop);
};
scala.collection.Iterable.prototype.sum = function() {
var self = this;
return self.foldLeft(0.0, function(x, y) {
return (x + y);
});
};
scala.collection.Iterable.prototype.product = function() {
var self = this;
return self.foldLeft(1.0, function(x, y) {
return (x * y);
});
};
scala.collection.Iterable.prototype.min = function() {
var self = this;
if (self.isEmpty()) {
(function() {
throw new scala.UnsupportedOperationException('empty.min');
})();
}

return self.reduceLeft(function(x, y) {
return (function() {
if ((x <= y)) {
return x;
} else {
return y;
}
})();
});

};
scala.collection.Iterable.prototype.max = function() {
var self = this;
if (self.isEmpty()) {
(function() {
throw new scala.UnsupportedOperationException('empty.max');
})();
}

return self.reduceLeft(function(x, y) {
return (function() {
if ((x >= y)) {
return x;
} else {
return y;
}
})();
});

};
scala.collection.Iterable.prototype.maxBy = function(f) {
var self = this;
if (self.isEmpty()) {
(function() {
throw new scala.UnsupportedOperationException('empty.maxBy');
})();
}

return self.reduceLeft(function(x, y) {
return (function() {
if ((f(x) >= f(y))) {
return x;
} else {
return y;
}
})();
});

};
scala.collection.Iterable.prototype.minBy = function(f) {
var self = this;
if (self.isEmpty()) {
(function() {
throw new scala.UnsupportedOperationException('empty.minBy');
})();
}

return self.reduceLeft(function(x, y) {
return (function() {
if ((f(x) <= f(y))) {
return x;
} else {
return y;
}
})();
});

};
scala.collection.Iterable.prototype.mkString = function(start, sep, end) {
var self = this;
var result = '';
var separator = '';
var suffix = '';
if ((! s2js.isUndefined(start))) {
result = start;
separator = sep;
suffix = end;

} else {
if ((! s2js.isUndefined(start))) {
separator = start;
}

}

var first = true;
self.foreach(function(x) {
if ((! first)) {
result = (result + separator);
}

result = (result + x.toString());
first = false;

});
result = (result + suffix);
return result;

};
scala.collection.Iterable.prototype.isEmpty = function() {
var self = this;
var result = true;
scala.util.control.Breaks.breakable(function() {
self.foreach(function(x) {
result = false;
scala.util.control.Breaks.$break();

});
});
return result;

};
scala.collection.Iterable.prototype.hasDefiniteSize = function() {
var self = this;
return true;
};
scala.collection.Iterable.prototype.$plus$plus = function(that) {
var self = this;
var b = self.newInstance();
b.$plus$plus$eq(self);
b.$plus$plus$eq(that);
return b;

};
scala.collection.Iterable.prototype.map = function(f) {
var self = this;
var b = self.newInstance();
self.foreach(function(x) {
b.$plus$eq(f(x));
});
return b;

};
scala.collection.Iterable.prototype.flatMap = function(f) {
var self = this;
var b = self.newInstance();
self.foreach(function(x) {
b.$plus$plus$eq(f(x));
});
return b;

};
scala.collection.Iterable.prototype.filter = function(p) {
var self = this;
var b = self.newInstance();
self.foreach(function(x) {
if (p(x)) {
b.$plus$eq(x);
}

});
return b;

};
scala.collection.Iterable.prototype.filterNot = function(p) {
var self = this;
return self.filter(function($x$2) {
return (! p($x$2));
});
};
scala.collection.Iterable.prototype.collect = function(pf) {
var self = this;
return (function() {
throw new scala.NotImplementedException('Function collect is not implemented.');
})();
};
scala.collection.Iterable.prototype.partition = function(p) {
var self = this;
var l = self.newInstance();
var r = self.newInstance();
self.foreach(function(x) {
(function() {
if (p(x)) {
return l;
} else {
return r;
}
})().$plus$eq(x);
});
return new scala.Tuple2(l, r);

};
scala.collection.Iterable.prototype.forall = function(p) {
var self = this;
var result = true;
scala.util.control.Breaks.breakable(function() {
self.foreach(function(x) {
if ((! p(x))) {
result = false;
scala.util.control.Breaks.$break();

}

});
});
return result;

};
scala.collection.Iterable.prototype.exists = function(p) {
var self = this;
return (! self.forall(function($x$3) {
return (! p($x$3));
}));
};
scala.collection.Iterable.prototype.find = function(p) {
var self = this;
var result = scala.None;
scala.util.control.Breaks.breakable(function() {
self.foreach(function(x) {
if (p(x)) {
result = new scala.Some(x);
scala.util.control.Breaks.$break();

}

});
});
return result;

};
scala.collection.Iterable.prototype.scan = function(z, op) {
var self = this;
return self.scanLeft(z, op);
};
scala.collection.Iterable.prototype.scanLeft = function(z, op) {
var self = this;
var b = self.newInstance();
var acc = z;
b.$plus$eq(acc);
self.foreach(function(x) {
acc = op(acc, x);
b.$plus$eq(acc);

});
return b;

};
scala.collection.Iterable.prototype.scanRight = function(z, op) {
var self = this;
var b = self.newInstance();
b.$plus$eq(z);
var acc = z;
self.reversed().foreach(function(x) {
acc = op(x, acc);
b.$plus$eq(acc);

});
return b;

};
scala.collection.Iterable.prototype.head = function() {
var self = this;
var result = function() {
return (function() {
throw new scala.NoSuchElementException('empty.head');
})();
};
scala.util.control.Breaks.breakable(function() {
self.foreach(function(x) {
result = function() {
return x;
};
scala.util.control.Breaks.$break();

});
});
return result();

};
scala.collection.Iterable.prototype.headOption = function() {
var self = this;
return (function() {
if (self.isEmpty()) {
return scala.None;
} else {
return new scala.Some(self.head());
}
})();
};
scala.collection.Iterable.prototype.tail = function() {
var self = this;
if (self.isEmpty()) {
(function() {
throw new scala.UnsupportedOperationException('empty.tail');
})();
}

return self.drop(1);

};
scala.collection.Iterable.prototype.last = function() {
var self = this;
var lst = self.head();
self.foreach(function(x) {
lst = x;
});
return lst;

};
scala.collection.Iterable.prototype.lastOption = function() {
var self = this;
return (function() {
if (self.isEmpty()) {
return scala.None;
} else {
return new scala.Some(self.last());
}
})();
};
scala.collection.Iterable.prototype.init = function() {
var self = this;
if (self.isEmpty()) {
(function() {
throw new scala.UnsupportedOperationException('empty.init');
})();
}

var lst = self.head();
var follow = false;
var b = self.newInstance();
self.foreach(function(x) {
if (follow) {
b.$plus$eq(lst);
} else {
follow = true;
}

lst = x;

});
return b;

};
scala.collection.Iterable.prototype.take = function(n) {
var self = this;
return self.slice(0, n);
};
scala.collection.Iterable.prototype.drop = function(n) {
var self = this;
return (function() {
if ((n <= 0)) {
var b = self.newInstance();
b.$plus$plus$eq(self);
return b;

} else {
return self.sliceWithKnownDelta(n, 2147483647, (- n));
}
})();
};
scala.collection.Iterable.prototype.dropRight = function(n) {
var self = this;
return (function() {
if ((n <= 0)) {
var b = self.newInstance();
return b;

} else {
return self.sliceWithKnownDelta(0, n, (- n));
}
})();
};
scala.collection.Iterable.prototype.slice = function(from, until) {
var self = this;
return self.sliceWithKnownBound(scala.math.max(from, 0), until);
};
scala.collection.Iterable.prototype.sliceInternal = function(from, until, b) {
var self = this;
var i = 0;
scala.util.control.Breaks.breakable(function() {
self.foreach(function(x) {
if ((i >= from)) {
b.$plus$eq(x);
}

i = (i + 1);
if ((i >= until)) {
scala.util.control.Breaks.$break();
}


});
});
return b;

};
scala.collection.Iterable.prototype.sliceWithKnownDelta = function(from, until, delta) {
var self = this;
var b = self.newInstance();
return (function() {
if ((until <= from)) {
return b;
} else {
return self.sliceInternal(from, until, b);
}
})();

};
scala.collection.Iterable.prototype.sliceWithKnownBound = function(from, until) {
var self = this;
var b = self.newInstance();
return (function() {
if ((until <= from)) {
return b;
} else {
return self.sliceInternal(from, until, b);
}
})();

};
scala.collection.Iterable.prototype.takeWhile = function(p) {
var self = this;
var b = self.newInstance();
scala.util.control.Breaks.breakable(function() {
self.foreach(function(x) {
if ((! p(x))) {
scala.util.control.Breaks.$break();
}

b.$plus$eq(x);

});
});
return b;

};
scala.collection.Iterable.prototype.dropWhile = function(p) {
var self = this;
var b = self.newInstance();
var go = false;
self.foreach(function(x) {
if ((! p(x))) {
go = true;
}

if (go) {
b.$plus$eq(x);
}


});
return b;

};
scala.collection.Iterable.prototype.span = function(p) {
var self = this;
var l = self.newInstance();
var r = self.newInstance();
var toLeft = true;
self.foreach(function(x) {
toLeft = (toLeft && p(x));
(function() {
if (toLeft) {
return l;
} else {
return r;
}
})().$plus$eq(x);

});
return new scala.Tuple2(l, r);

};
scala.collection.Iterable.prototype.splitAt = function(n) {
var self = this;
var l = self.newInstance();
var r = self.newInstance();
var i = 0;
self.foreach(function(x) {
(function() {
if ((i < n)) {
return l;
} else {
return r;
}
})().$plus$eq(x);
i = (i + 1);

});
return new scala.Tuple2(l, r);

};
scala.collection.Iterable.prototype.toString = function() {
var self = this;
return self.mkString((self.stringPrefix() + '('), ', ', ')');
};
scala.collection.Iterable.prototype.stringPrefix = function() {
var self = this;
var str = s2js.classOf(self).fullName;
var idx1 = str.lastIndexOf(46);
if ((idx1 != -1)) {
str = str.substring((idx1 + 1));
}

var idx2 = str.indexOf(36);
if ((idx2 != -1)) {
str = str.substring(0, idx2);
}

return str;

};
scala.collection.Iterable.prototype.__class__ = new s2js.Class('scala.collection.Iterable', []);
