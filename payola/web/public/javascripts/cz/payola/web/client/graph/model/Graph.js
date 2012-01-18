goog.provide('cz.payola.web.client.graph.model.Graph');
cz.payola.web.client.graph.model.Graph = function(vertices, edges) {
var self = this;
self.vertices = vertices;
self.edges = edges;
};
cz.payola.web.client.graph.model.Graph.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.model.Graph', []);
