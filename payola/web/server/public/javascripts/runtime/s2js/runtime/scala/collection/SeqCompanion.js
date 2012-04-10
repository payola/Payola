goog.provide('scala.collection.SeqCompanion');
scala.collection.SeqCompanion = function() {
var self = this;
};
scala.collection.SeqCompanion.prototype.fromJsArray = function(jsArray) {
var self = this;

        var a = self.empty();
        a.setInternalJsArray(jsArray);
        return a;
    };
scala.collection.SeqCompanion.prototype.__class__ = new s2js.Class('scala.collection.SeqCompanion', []);
