goog.provide('cz.payola.common.rdf.Graph');
cz.payola.common.rdf.Graph = function() {
var self = this;
};
cz.payola.common.rdf.Graph.prototype.getOutgoingEdges = function(vertexUri) {
var self = this;
return self.edges.filter(function($x$1) {
return ($x$1.origin.uri == vertexUri);
});
};
cz.payola.common.rdf.Graph.prototype.getIncomingEdges = function(vertexUri) {
var self = this;
return self.edges.filter(function($x$2) {
return ($x$2.destination.uri == vertexUri);
});
};
cz.payola.common.rdf.Graph.prototype.__class__ = new s2js.Class('cz.payola.common.rdf.Graph', []);
