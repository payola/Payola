package cz.payola.graphDrawer

import graphStructure.Vertex
import util.Color

/**
 * Created by IntelliJ IDEA.
 * User: Ondrej Kudlacek
 * Date: 12/14/11
 * Time: 1:04 AM
 */

object Drawer {

  import util.Constants._;

  def drawEdge(x1: Int, y1: Int, x2: Int, y2: Int, colorToUse: Color, ctx: Context) {

    ctx.strokeStyle = colorToUse.toString;
    ctx.lineWidth = EDGE_WIDTH;
    ctx.beginPath();

    var ctrl1X = 0;
    var ctrl1Y = 0;
    var ctrl2X = 0;
    var ctrl2Y = 0;

    val diffX = scala.math.abs (x1 - x2);
    //diffX = diffX < 0 ? (diffX*(-1)) : diffX;
    val diffY = scala.math.abs(y1 - y2);
    //diffY = diffY < 0 ? (diffY*(-1)) : diffY;

    //quadrant of coordinate system: 1) right bottom 2) left bottom 3) left top 4) right top
    val quadrant = { //quadrant of destination
      if(x1 <= x2) {
        if(y1 <= y2) { 1 } else { 4 }
      } else {
        if(y1 <= y2) { 2 } else { 3 }
      }
    }

    if(diffX >= diffY) {
      //connecting left/right sides of vertices
      quadrant match {
        case 1 | 4 => //we are in (0, pi/4] or in (pi7/4, 2pi]
          ctrl1X = x1 + diffX/EDGE_S_INDEX;
          ctrl1Y = y1;
          ctrl2X = x2 - diffX/EDGE_S_INDEX;
          ctrl2Y = y2;
        case 2 | 3 => //we are in (pi3/4, pi] or in (pi, pi5/4]
          ctrl1X = x1 - diffX/EDGE_S_INDEX;
          ctrl1Y = y1;
          ctrl2X = x2 + diffX/EDGE_S_INDEX;
          ctrl2Y = y2;
      }

    } else {
      //connecting top/bottom sides of vertices
      quadrant match {
        case 1 | 2 => //we are in (pi/4, pi/2] or in (pi/2, pi3/4]
          ctrl1X = x1;
          ctrl1Y = y1 + diffY/EDGE_S_INDEX;
          ctrl2X = x2;
          ctrl2Y = y2 - diffY/EDGE_S_INDEX;
        case 3 | 4 => //we are in (pi5/4, pi3/2] or in (pi3/2, pi7/4]
          ctrl1X = x1;
          ctrl1Y = y1 - diffY/EDGE_S_INDEX;
          ctrl2X = x2;
          ctrl2Y = y2 + diffY/EDGE_S_INDEX;
      }
    }

    ctx.moveTo(x1, y1);
    ctx.bezierCurveTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, x2, y2);
    ctx.stroke();
  }

  def drawVertex(locationX: Int, locationY: Int, text: String, colorToUse: Color, ctx: Context) {

    //theory:
    //	ctx.quadraticCurveTo(
    //		bend X coord (control point), bend Y coord (control point),
    //		end point X coord, end point Y);

    //size of global.vertexRadius:drawEdge
    //	the bigger, the corners are rounder;
    //	if x > Min(global.vertexWidth, global.vertexHeight) / 2, draws edges
    //		of the vertex over the original rectangle dimensions
    //	if x < 0, draws the arches mirrored to the edges of the original rectangle
    //		(in the mirrored quadrant)...ehm, looks interesting :-)


    val x1 = locationX - VERTEX_WIDTH / 2;
    val y1 = locationY - VERTEX_HEIGHT / 2;

    ctx.beginPath();

    var aX = x1 + VERTEX_RADIUS;
    var aY = y1;
    ctx.moveTo(aX, aY);

    aX = x1; aY = y1;
    ctx.quadraticCurveTo(aX, aY, aX, aY + VERTEX_RADIUS); //upper left

    aX = x1; aY = y1 + VERTEX_HEIGHT;
    ctx.lineTo(aX, aY - VERTEX_RADIUS);
    ctx.quadraticCurveTo(aX, aY, aX + VERTEX_RADIUS, aY); //lower left


    aX = x1 + VERTEX_WIDTH; aY = y1 + VERTEX_HEIGHT;
    ctx.lineTo(aX - VERTEX_RADIUS, aY);
    ctx.quadraticCurveTo(aX, aY, aX, aY - VERTEX_RADIUS); //lower right

    aX = x1 + VERTEX_WIDTH; aY = y1;
    ctx.lineTo(aX, aY + VERTEX_RADIUS);
    ctx.quadraticCurveTo(aX, aY, aX - VERTEX_RADIUS, aY); //upper right

    ctx.closePath();

    ctx.fillStyle = colorToUse.toString;
    ctx.fill();

    if(! text.isEmpty) {
      ctx.fillStyle = COLOR_TEXT.toString;
      ctx.font = "18px Sans";
      ctx.textAlign = "center";
      ctx.fillText(text, locationX + TEXT_COORD_CORRECTION_X,
        locationY + TEXT_COORD_CORRECTION_Y);
    }
  }

  def drawSelectionByRect(x1: Int, y1: Int, x2: Int, y2: Int, colorToUse: Color, ctx: Context) {

    ctx.strokeStyle = colorToUse.toString;
    ctx.lineWidth = SELECT_LINE_WIDTH;
    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x1, y2);
    ctx.lineTo(x2, y2);
    ctx.lineTo(x2, y1);
    ctx.lineTo(x1, y1);
    ctx.closePath();
    ctx.stroke();
  }

  def drawGraph(graph: List[Vertex], ctx: Context) {

    var somethingSelected = false;
    
    //need to draw edges first, so that edges would not be drawn "over" vertices
    graph.foreach {
      (vertexA: Vertex) =>

        if(vertexA.selected) {
          somethingSelected = true;
        }
      //kdyz vrchol A nema sousedy nebudu je hledat
        if (vertexA.neighbours.isEmpty) {
          return; //TODO test jestli tohle skutecne napodobi continue
        }
        //pro vrchol A najdu vsechny vrcholy, ktere jsou jeho sousedy
        graph.foreach {
          (vertexB: Vertex) =>
            
          //projdu seznam sousedu A
          vertexA.neighbours.foreach {
            (vertexNeighbour: Vertex) =>
                    
            //pokud vrchol B je v seznamu sousedu A nakreslim mezi vrcholy caru
            if(vertexB.id == vertexNeighbour.id) {
              if(vertexA.id < vertexB.id) { //otherwise the edge is already drawn
                drawEdge(vertexA.X, vertexA.Y, vertexB.X, vertexB.Y,
                  if(vertexA.selected || vertexB.selected) { COLOR_EDGE_SELECT } else { COLOR_EDGE },
                  ctx);
              }
            }
          }
        }
    }

    var neighbourSelected = false;
    graph.foreach {
      (vertexA: Vertex) =>

      neighbourSelected = false;
      graph.foreach {
        (vertexB: Vertex) =>
          
        //projdu seznam sousedu A
        vertexA.neighbours.foreach {
          (vertexNeighbour: Vertex) =>
            
          if(vertexB.id == vertexNeighbour.id && vertexB.selected) {
            neighbourSelected = true;
            return; //TODO test jestli tohle skutecne funguje jako break;
          }
        }
        if(neighbourSelected) {
          return; //TODO test jestli tohle skutecne funguje jako break;
        }
      }

      if(vertexA.selected) {
        drawVertex(vertexA.X, vertexA.Y, vertexA.textContent, COLOR_VERTEX_HIGH, ctx);
      } else if(!somethingSelected) {
        drawVertex(vertexA.X, vertexA.Y, vertexA.textContent, COLOR_VERTEX, ctx);
      } else if(neighbourSelected) {
        drawVertex(vertexA.X, vertexA.Y, "", COLOR_VERTEX_MEDIUM, ctx);
      } else { //no neighbour nor this vertex is selected, but something else is
        drawVertex(vertexA.X, vertexA.Y, "", COLOR_VERTEX_LOW, ctx);
      }
    }          
  }

  def clear(x: Int, y: Int, width: Int, height: Int, ctx: Context) {

    ctx.fillStyle = COLOR_BACKGROUND.toString;
    ctx.fillRect(x, y, width, height);
  }

  def redraw(graph: List[Vertex], ctx: Context) {

    clear(0, 0, 700, 700, ctx); //TODO podminene vykreslovani jen casti, ktere je nutne predelat
    drawGraph(graph, ctx);
  }
}