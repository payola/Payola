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
class ZoomableTreemap(prefixApplier: Option[PrefixApplier] = None) extends PluginView[Any]("Zoomable Treemap", prefixApplier) {

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
                        name: getName(entity)
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


          var w = 1280 - 80,
              h = 800 - 180,
              x = d3.scale.linear().range([0, w]),
              y = d3.scale.linear().range([0, h]),
              color = d3.scale.category20c(),
              root,
              node;

          var treemap = d3.layout.treemap()
              .round(false)
              .size([w, h])
              .sticky(true)
              .value(function(d) { return d.size; });

          var svg = d3.select(self.d3Placeholder.blockHtmlElement()).append("div")
              .attr("class", "chart")
              .style("width", w + "px")
              .style("height", h + "px")
            .append("svg:svg")
              .attr("width", w)
              .attr("height", h)
            .append("svg:g")
              .attr("transform", "translate(.5,.5)");

            node = root = data;

            var nodes = treemap.nodes(root)
                .filter(function(d) { return !d.children; }).getInternalJsArray();

            var cell = svg.selectAll("g")
                .data(nodes)
                .enter().append("svg:g")
                .attr("class", "cell")
                .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
                .on("click", function(d) { return zoom(node == d.parent ? root : d.parent); });

            cell.append("svg:title")
                .text(function(d){return d.name+": "+d.size;})

            cell.append("svg:rect")
                .attr("width", function(d) { return d.dx - 1; })
                .attr("height", function(d) { return d.dy - 1; })
                .style("fill", function(d) { return color(d.parent.name); });

            cell.append("svg:text")
                .attr("x", function(d) { return d.dx / 2; })
                .attr("y", function(d) { return d.dy / 2; })
                .attr("dy", ".35em")
                .attr("text-anchor", "middle")
                .text(function(d) { return d.name; })
                .style("opacity", function(d) { d.w = this.getComputedTextLength(); return d.dx > d.w ? 1 : 0; });

            d3.select(window).on("click", function() { zoom(root); });

            d3.select("select").on("change", function() {
              treemap.value(this.value == "size" ? size : count).nodes(root);
              zoom(node);
            });

          function size(d) {
            return d.size;
          }

          function count(d) {
            return 1;
          }

          function zoom(d) {
            var kx = w / d.dx, ky = h / d.dy;
            x.domain([d.x, d.x + d.dx]);
            y.domain([d.y, d.y + d.dy]);

            var t = svg.selectAll("g.cell").transition()
                .duration(d3.event.altKey ? 7500 : 750)
                .attr("transform", function(d) { return "translate(" + x(d.x) + "," + y(d.y) + ")"; });

            t.select("rect")
                .attr("width", function(d) { return kx * d.dx - 1; })
                .attr("height", function(d) { return ky * d.dy - 1; })

            t.select("text")
                .attr("x", function(d) { return kx * d.dx / 2; })
                .attr("y", function(d) { return ky * d.dy / 2; })
                .style("opacity", function(d) { return kx * d.dx > d.w ? 1 : 0; });

            node = d;
            d3.event.stopPropagation();
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
