package cz.payola.web.client.views.d3.packLayout

import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.graph.PluginView
import s2js.compiler.javascript
import s2js.adapters.html
import cz.payola.web.shared.transformators.RdfJsonTransformator
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal

/**
 * @author Jiri Helmich
 */
class ZoomableSunburst(prefixApplier: Option[PrefixApplier] = None) extends PluginView[Any]("Zoomable Sunburst", prefixApplier) {

    val d3Placeholder = new Div(List())
    val placeholder = new Div(List(d3Placeholder))
    d3Placeholder.setAttribute("id","d3-placeholder")

    private var _serializedGraph : Any = ""
    private var _rendered = false

    @javascript("""console.log(str)""")
    def log(str: Any) {}

    @javascript(""" return parseInt(str); """)
    def intval(str: String) : Int = 0


    override def updateSerializedGraph(serializedGraph: Option[Any]) {
        serializedGraph.map{ sg =>
            _serializedGraph = sg
            d3Placeholder.removeAllChildNodes()
            if(_rendered) { parseJSON(_serializedGraph) }
        }
    }

    @javascript(
        """
            var skos = function(s){ return "http://www.w3.org/2004/02/skos/core#"+s; };
            var rdf = function(s) { return "http://www.w3.org/1999/02/22-rdf-syntax-ns#"+s; };

          function randomColor(brightness){
            function randomChannel(brightness){
              var r = 255-brightness;
              var n = 0|((Math.random() * r) + brightness);
              var s = n.toString(16);
              return (s.length==1) ? '0'+s : s;
            }
            return '#' + randomChannel(brightness) + randomChannel(brightness) + randomChannel(brightness);
          }

            var objMap = {};
            var queue = [];

            var data = null;

            for(var i in json){
                var entity = json[i];

                if (entity[rdf("type")] && (entity[rdf("type")][0].value == skos("Concept"))){

                    function getName(entity){
                        if(entity[skos("prefLabel")] && entity[skos("prefLabel")][0] && entity[skos("prefLabel")][0].value){
                            return entity[skos("prefLabel")][0].value;
                        }
                        return "no name";
                    }

                    var o = {
                        name: getName(entity),
                        colour: randomColor(220)
                    };

                    if(entity[skos("broader")]){
                        for(var n in entity[skos("broader")]){
                            queue.push({entity:entity, child: o, callback: function(entity, child){
                                var obj = objMap[entity[skos("broader")][n].value];
                                if(!obj) return;
                                if(!obj.children) { obj.children = []; }
                                obj.children.push(child);
                            }});
                        }
                    }

                    if(entity[rdf("value")]){
                        o.size = (((entity[rdf("value")] || [{value:1}])[0]) || {value: 1}).value;
                    }else{
                        o.size = 1;
                    }

                    if (!entity[skos("broader")] && !entity[rdf("value")]){
                        data = o;
                    }

                    objMap[i] = o;
                }
            };

            for (var c in queue){
                var q = queue[c];
                q.callback(q.entity, q.child);
            }

                                                var root = data;

          var width = 840,
              height = width,
              radius = width / 2,
              x = d3.scale.linear().range([0, 2 * Math.PI]),
              y = d3.scale.pow().exponent(1.3).domain([0, 1]).range([0, radius]),
              padding = 5,
              duration = 1000;

          var div = d3.select(self.d3Placeholder.blockHtmlElement());

          var vis = div.append("svg")
              .attr("width", width + padding * 2)
              .attr("height", height + padding * 2)
            .append("g")
              .attr("transform", "translate(" + (radius + padding) + ","+ (radius + padding) + ")");

          div.append("p")
              .attr("id", "intro")
              .text("Click to zoom!");

          var partition = d3.layout.partition()
              .sort(function(d) { return d.size; })
              .value(function(d) { return d.size; });

          var arc = d3.svg.arc()
              .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
              .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
              .innerRadius(function(d) { return Math.max(0, d.y ? y(d.y) : d.y); })
              .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });

            var nodes = partition.nodes(root);

            var path = vis.selectAll("path").data(nodes);
            path.enter().append("path")
                .attr("id", function(d, i) { return "path-" + i; })
                .attr("d", arc)
                .attr("fill-rule", "evenodd")
                .style("fill", colour)
                .on("click", click)
                .append("svg:title")
                .text(function(d){return d.name+": "+d.size;});

            var text = vis.selectAll("text").data(nodes);
            var textEnter = text.enter().append("text")
                .style("fill-opacity", 1)
                .style("fill", function(d) {
                  return brightness(d3.rgb(colour(d))) < 125 ? "#eee" : "#000";
                })
                .attr("text-anchor", function(d) {
                  return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
                })
                .attr("dy", ".2em")
                .attr("transform", function(d) {
                  var multiline = (d.name || "").split(" ").length > 1,
                      angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
                      rotate = angle + (multiline ? -.5 : 0);
                  return "rotate(" + rotate + ")translate(" + (y(d.y) + padding) + ")rotate(" + (angle > 90 ? -180 :
                  0) + ")";
                })
                .on("click", click);
            textEnter.append("tspan")
                .attr("x", 0)
                .text(function(d) { return d.depth ? d.name.split(" ")[0] : ""; });
            textEnter.append("tspan")
                .attr("x", 0)
                .attr("dy", "1em")
                .text(function(d) { return d.depth ? d.name.split(" ")[1] || "" : ""; });

            function click(d) {
              path.transition()
                .duration(duration)
                .attrTween("d", arcTween(d));

              // Somewhat of a hack as we rely on arcTween updating the scales.
              text.style("visibility", function(e) {
                    return isParentOf(d, e) ? null : d3.select(this).style("visibility");
                  })
                .transition()
                  .duration(duration)
                  .attrTween("text-anchor", function(d) {
                    return function() {
                      return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
                    };
                  })
                  .attrTween("transform", function(d) {
                    var multiline = (d.name || "").split(" ").length > 1;
                    return function() {
                      var angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
                          rotate = angle + (multiline ? -.5 : 0);
                      return "rotate(" + rotate + ")translate(" + (y(d.y) + padding) + ")rotate(" + (angle > 90 ?
                      -180 : 0) + ")";
                    };
                  })
                  .style("fill-opacity", function(e) { return isParentOf(d, e) ? 1 : 1e-6; })
                  .each("end", function(e) {
                    d3.select(this).style("visibility", isParentOf(d, e) ? null : "hidden");
                  });
            }

          function isParentOf(p, c) {
            if (p === c) return true;
            if (p.children) {
              return p.children.some(function(d) {
                return isParentOf(d, c);
              });
            }
            return false;
          }

          function colour(d) {
            if (d.children) {
              // There is a maximum of two children!
              var colours = d.children.map(colour).getInternalJsArray(),
                  a = d3.hsl(colours[0]),
                  b = d3.hsl(colours[1]);
              // L*a*b* might be better here...
              return d3.hsl((a.h + b.h) / 2, a.s * 1.2, a.l / 1.2);
            }
            return d.colour || "#fff";
          }

          // Interpolate the scales!
          function arcTween(d) {
            var my = maxY(d),
                xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
                yd = d3.interpolate(y.domain(), [d.y, my]),
                yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
            return function(d) {
              return function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
            };
          }

          function maxY(d) {
            return d.children ? Math.max.apply(Math, d.children.map(maxY).getInternalJsArray()) : d.y + d.dy;
          }

          // http://www.w3.org/WAI/ER/WD-AERT/#color-contrast
          function brightness(rgb) {
            return rgb.r * .299 + rgb.g * .587 + rgb.b * .114;
          }


        """)
    def parseJSON(json: Any) {}

    def createSubViews = List(placeholder)

    override def render(parent: html.Element) {
        subViews.foreach { v =>
            new Text(" ").render(parent)
            v.render(parent)
        }
        parseJSON(_serializedGraph)
        _rendered = true
    }

    override def isAvailable(availableTransformators: List[String], evaluationId: String,
        success: () => Unit, fail: () => Unit) {

        success() //TODO whe is available????
    }

    override def loadDefaultCachedGraph(evaluationId: String, updateGraph: Option[Any] => Unit) {
        RdfJsonTransformator.getCompleteGraph(evaluationId)(updateGraph(_)) //TODO default graph and paginating
        { error =>
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }
}
