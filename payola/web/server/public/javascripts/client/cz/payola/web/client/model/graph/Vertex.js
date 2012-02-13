goog.provide('cz.payola.web.client.model.graph.Vertex');
goog.require('cz.payola.web.client.model.graph.ModelObject');
cz.payola.web.client.model.graph.Vertex = function(uri) {
var self = this;
self.uri = uri;
goog.base(self);};
goog.inherits(cz.payola.web.client.model.graph.Vertex, cz.payola.web.client.model.graph.ModelObject);
cz.payola.web.client.model.graph.Vertex.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.model.graph.Vertex', [cz.payola.web.client.model.graph.ModelObject]);
