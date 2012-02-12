goog.provide('cz.payola.web.client.model.graph.Edge');
goog.require('cz.payola.web.client.model.graph.ModelObject');
cz.payola.web.client.model.graph.Edge = function(uri, origin, destination) {
var self = this;
self.uri = uri;
self.origin = origin;
self.destination = destination;
goog.base(self);};
goog.inherits(cz.payola.web.client.model.graph.Edge, cz.payola.web.client.model.graph.ModelObject);
cz.payola.web.client.model.graph.Edge.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.model.graph.Edge', [cz.payola.web.client.model.graph.ModelObject]);
