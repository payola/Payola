package cz.payola.graphDrawer.graphStructure

/**
 * Created by IntelliJ IDEA.
 * User: Ondrej Kudlacek
 * Date: 12/14/11
 * Time: 1:06 AM
 */

/**
 * djwalkdjalkjs
 */
class Vertex(identity: Int,
             coordinateX: Int,  coordinateY: Int,
             text: String, neighbourVertices: List[Vertex]) {

  val id = identity;

  var X = coordinateX;
  var Y = coordinateY;

  var textContent = text;

  var neighbours: List[Vertex] = neighbourVertices;

  var selected = false;

}