package cz.payola.web.client.views.map.libwrappers

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.map.Marker
import s2js.adapters.dom.Element
import s2js.compiler.javascript
import cz.payola.web.client.views.map.facets.MapFacet

/**
 * Google Maps wrapper, mostly written in JavaScript. Just creates subviews and renders.
 * @author Jiri Helmich
 */
class ArcGisMapsWrapper(center: Coordinates, facets: Seq[MapFacet], markerData: Seq[Marker], element: Element) extends ComposedView
{
    val mapPlaceholder = new Div(List(), "map")
        .setAttribute("data-dojo-type", "dijit/layout/ContentPane")
        .setAttribute("data-dojo-props", "region:'center'")
        .setAttribute("id","map")

    val borderContainer = new Div(List(mapPlaceholder), "border")
        .setAttribute("data-dojo-type", "dijit/layout/BorderContainer")
        .setAttribute("data-dojo-props", "design:'headline',gutters:false")
        .setAttribute("style", "width: 100%; height: 100%; margin: 0;")

    def createSubViews = {
        loadMapsScript

        List(borderContainer)
    }

    /**
     * Init GMaps API
     */
    @javascript(
        """
               window.mapCallback = self.createMap
               window.selfContext = self

               window.mapCallback();

        """)
    def loadMapsScript {}

    /**
     * Create map instance.
     */
    @javascript( """
                var map;
                require([
                   "dojo/parser",
                   "dojo/ready",
                   "dojo/_base/array",
                   "dojo/_base/Color",
                   "dojo/dom-style",
                   "dojo/query",

                   "esri/map",
                   "esri/request",
                   "esri/graphic",
                   "esri/geometry/Extent",

                   "esri/symbols/SimpleMarkerSymbol",
                   "esri/symbols/SimpleFillSymbol",
                   "esri/symbols/PictureMarkerSymbol",
                   "esri/renderers/ClassBreaksRenderer",

                   "esri/layers/GraphicsLayer",
                   "esri/SpatialReference",
                   "esri/dijit/PopupTemplate",
                   "esri/geometry/Point",
                   "esri/geometry/webMercatorUtils",

                   "extras/ClusterLayer",

                   "dijit/layout/BorderContainer",
                   "dijit/layout/ContentPane",
                   "dojo/domReady!"
                 ], function(
                   parser, ready, arrayUtils, Color, domStyle, query,
                   Map, esriRequest, Graphic, Extent,
                   SimpleMarkerSymbol, SimpleFillSymbol, PictureMarkerSymbol, ClassBreaksRenderer,
                   GraphicsLayer, SpatialReference, PopupTemplate, Point, webMercatorUtils,
                   ClusterLayer
                 ) {
                   ready(function() {
                     parser.parse();

                     var clusterLayer;
                     var popupOptions = {
                       "markerSymbol": new SimpleMarkerSymbol("circle", 20, null, new Color([0, 0, 0, 0.25])),
                       "marginLeft": "20",
                       "marginTop": "20"
                     };
                     map = new Map("map", {
                       basemap: "topo",
                       center: [0, 0],
                       zoom: 3
                     });

                     map.on("load", function() {
                       // hide the popup's ZoomTo link as it doesn't make sense for cluster features
                       domStyle.set(query("a.action.zoomTo")[0], "display", "none");

                       addClusters(window.selfContext.markerData);
                     });

                     function addClusters(markers) {
                       var info = {};
                       var wgs = new SpatialReference({
                         "wkid": 4326
                       });

                       info.data = arrayUtils.map(markers.getInternalJsArray(), function(m) {
                         var latlng = new  Point(parseFloat(m.coordinates.lng), parseFloat(m.coordinates.lat), wgs);
                         var webMercator = webMercatorUtils.geographicToWebMercator(latlng);
                         var attributes = {
                           "Caption": m.description,
                           "Name": m.title,
                           "Link": ""
                         };
                         return {
                           "x": webMercator.x,
                           "y": webMercator.y,
                           "attributes": attributes
                         };
                       });

                       // popupTemplate to work with attributes specific to this dataset
                       var popupTemplate = PopupTemplate({
                         "title": "",
                         "fieldInfos": [{
                           "fieldName": "Caption",
                           visible: true
                         }, {
                           "fieldName": "Name",
                           "label": "By",
                           visible: true
                         }, {
                           "fieldName": "Link",
                           "label": "Link",
                           visible: true
                         }],
                         "mediaInfos": []
                       });

                       // cluster layer that uses OpenLayers style clustering
                       clusterLayer = new ClusterLayer({
                         "data": info.data,
                         "distance": 100,
                         "id": "clusters",
                         "labelColor": "#fff",
                         "labelOffset": 10,
                         "resolution": map.extent.getWidth() / map.width,
                         "singleColor": "#888",
                         "singleTemplate": popupTemplate
                       });
                       var defaultSym = new SimpleMarkerSymbol().setSize(4);
                       var renderer = new ClassBreaksRenderer(defaultSym, "clusterCount");

                       var picBaseUrl = "http://static.arcgis.com/images/Symbols/Shapes/";
                       var blue = new PictureMarkerSymbol(picBaseUrl + "BluePin1LargeB.png", 32, 32).setOffset(0, 15);
                       var green = new PictureMarkerSymbol(picBaseUrl + "GreenPin1LargeB.png", 64, 64).setOffset(0, 15);
                       var red = new PictureMarkerSymbol(picBaseUrl + "RedPin1LargeB.png", 72, 72).setOffset(0, 15);
                       var yellow = new PictureMarkerSymbol(picBaseUrl + "YellowPin1LargeB.png", 72, 72).setOffset(0, 15);
                       var orange = new PictureMarkerSymbol(picBaseUrl + "OrangePin1LargeB.png", 72, 72).setOffset(0, 15);
                       renderer.addBreak(0, 2, blue);
                       renderer.addBreak(2, 200, green);
                       renderer.addBreak(200, 1000, yellow);
                       renderer.addBreak(1000, 5000, orange);
                       renderer.addBreak(5000, 1000000, red);

                       clusterLayer.setRenderer(renderer);
                       map.addLayer(clusterLayer);

                       // close the info window when the map is clicked
                       map.on("click", cleanUp);
                       // close the info window when esc is pressed
                       map.on("key-down", function(e) {
                         if (e.keyCode === 27) {
                           cleanUp();
                         }
                       });
                     }

                     function cleanUp() {
                       map.infoWindow.hide();
                       clusterLayer.clearSingles();
                     }

                     function error(err) {
                       console.log("something failed: ", err);
                     }

                     // show cluster extents...
                     // never called directly but useful from the console
                     window.showExtents = function() {
                       var extents = map.getLayer("clusterExtents");
                       if ( extents ) {
                         map.removeLayer(extents);
                       }
                       extents = new GraphicsLayer({ id: "clusterExtents" });
                       var sym = new SimpleFillSymbol().setColor(new Color([205, 193, 197, 0.5]));

                       arrayUtils.forEach(clusterLayer._clusters, function(c, idx) {
                         var e = c.attributes.extent;
                         extents.add(new Graphic(new Extent(e[0], e[1], e[2], e[3], map.spatialReference),
                         sym));
                       }, this);
                       map.addLayer(extents, 0);
                     }
                   });
                 });

                 """)
    def createMap {}
}
