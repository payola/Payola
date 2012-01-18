goog.provide('cz.payola.web.client.graph.model.Edge');
cz.payola.web.client.graph.model.Edge = function(uri, from, to) {
var self = this;
self.uri = uri;
self.from = from;
self.to = to;
};
cz.payola.web.client.graph.model.Edge.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.model.Edge', []);
