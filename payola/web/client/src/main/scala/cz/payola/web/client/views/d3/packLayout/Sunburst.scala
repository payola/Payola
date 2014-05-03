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
class Sunburst(prefixApplier: Option[PrefixApplier] = None) extends PluginView[Any]("Sunburst", prefixApplier) {

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
                        name: getName(entity),
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

         var width = 960,
              height = 700,
              radius = Math.min(width, height) / 2,
              color = d3.scale.category20c();

          var svg = d3.select(self.d3Placeholder.blockHtmlElement()).append("svg")
              .attr("width", width)
              .attr("height", height)
            .append("g")
              .attr("transform", "translate(" + width / 2 + "," + height * .52 + ")");

          var partition = d3.layout.partition()
              .sort(null)
              .size([2 * Math.PI, radius * radius])
              .value(function(d) { return d.size; });

          var arc = d3.svg.arc()
              .startAngle(function(d) { return d.x; })
              .endAngle(function(d) { return d.x + d.dx; })
              .innerRadius(function(d) { return Math.sqrt(d.y); })
              .outerRadius(function(d) { return Math.sqrt(d.y + d.dy); });

            var path = svg.datum(root).selectAll("path")
                .data(partition.nodes)
                .enter().append("path")
                .attr("display", function(d) { return d.depth ? null : "none"; }) // hide inner ring
                .attr("d", arc)
                .style("stroke", "#fff")
                .style("fill", function(d) { return color((d.children ? d : d.parent).name); })
                .style("fill-rule", "evenodd")
                .each(stash);

            path.append("svg:title")
                .text(function(d){return d.name+": "+d.size;});

            /*d3.selectAll("input").on("change", function change() {
              var value = this.value === "count"
                  ? function() { return 1; }
                  : function(d) { return d.size; };

              path
                  .data(partition.value(value).nodes)
                .transition()
                  .duration(1500)
                  .attrTween("d", arcTween);
            });  */
          //});

          // Stash the old values for transition.
          function stash(d) {
            d.x0 = d.x;
            d.dx0 = d.dx;
          }

          // Interpolate the arcs in data space.
          function arcTween(a) {
            var i = d3.interpolate({x: a.x0, dx: a.dx0}, a);
            return function(t) {
              var b = i(t);
              a.x0 = b.x;
              a.dx0 = b.dx;
              return arc(b);
            };
          }

          //d3.select(self.frameElement).style("height", height + "px");

          //}


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
