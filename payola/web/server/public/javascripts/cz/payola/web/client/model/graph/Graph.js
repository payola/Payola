goog.provide('cz.payola.web.client.model.graph.Graph');
cz.payola.web.client.model.graph.Graph = function(vertices, edges) {
var self = this;
self.vertices = vertices;
self.edges = edges;
};
cz.payola.web.client.model.graph.Graph.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.model.graph.Graph', []);
