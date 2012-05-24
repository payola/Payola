package cz.payola.web.client.views.plugins.visual.graph

import cz.payola.common.rdf.Edge
import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.plugins.visual._
import settings.{TextSettingsModel, EdgeSettingsModel}

/**
  * Structure used during draw function of EdgeView. Helps to indicate position of vertices to each other.
  */
private object Quadrant
{
    //TODO correct to real enum
    val RightBottom = 1

    val LeftBottom = 2

    val LeftTop = 3

    val RightTop = 4
}

/**
  * Graphical representation of Edge object in the drawn graph.
  * @param edgeModel the object graphically represented by this class
  * @param originView the vertex object representing origin of this edge
  * @param destinationView of this graphical representation in drawing space
  */
class EdgeView(val edgeModel: Edge, val originView: VertexView, val destinationView: VertexView,
    val settings: EdgeSettingsModel, settingsText: TextSettingsModel) extends View {
    
    /**
      * Textual data that should be visualised with this edge ("over this edge").
      */
    val information: InformationView = new InformationView(edgeModel, settingsText)

    /**
      * Indicator of selection of this graphs element. Is used during color selection in draw function.
      * @return true if one of the edges vertices is selected.
      */
    def isSelected: Boolean = {
        originView.selected || destinationView.selected
    }

    /**
      * Indicator of selection of this graphs element. Is not used by inner mechanics.
      * @return true if both edges vertices are selected.
      */
    def areBothVerticesSelected: Boolean = {
        originView.selected && destinationView.selected
    }

    private def prepareBezierCurve(context: CanvasRenderingContext2D, color: Color, correction: Vector) {

        val A = originView.position
        val B = destinationView.position

        val ctrl1 = Point(0, 0)
        val ctrl2 = Point(0, 0)

        val diff = Point(scala.math.abs(A.x - B.x), scala.math.abs(A.y - B.y))

        //quadrant of coordinate system
        val quadrant = {
            //quadrant of destination
            if (A.x <= B.x) {
                if (A.y <= B.y) {
                    Quadrant.RightBottom
                } else {
                    Quadrant.RightTop
                }
            } else {
                if (A.y <= B.y) {
                    Quadrant.LeftBottom
                } else {
                    Quadrant.LeftTop
                }
            }
        }

        if (diff.x >= diff.y) {
            //connecting left/right sides of vertices
            quadrant match {
                case Quadrant.RightBottom | Quadrant.RightTop =>
                    //we are in (0, pi/4] or in (pi7/4, 2pi]
                    ctrl1.x = A.x + diff.x / settings.straightenIndex
                    ctrl1.y = A.y
                    ctrl2.x = B.x - diff.x / settings.straightenIndex
                    ctrl2.y = B.y
                case Quadrant.LeftBottom | Quadrant.LeftTop =>
                    //we are in (pi3/4, pi] or in (pi, pi5/4]
                    ctrl1.x = A.x - diff.x / settings.straightenIndex
                    ctrl1.y = A.y
                    ctrl2.x = B.x + diff.x / settings.straightenIndex
                    ctrl2.y = B.y
            }
        } else {
            //connecting top/bottom sides of vertices
            quadrant match {
                case Quadrant.RightBottom | Quadrant.LeftBottom =>
                    //we are in (pi/4, pi/2] or in (pi/2, pi3/4]
                    ctrl1.x = A.x
                    ctrl1.y = A.y + diff.y / settings.straightenIndex
                    ctrl2.x = B.x
                    ctrl2.y = B.y - diff.y / settings.straightenIndex
                case Quadrant.RightTop | Quadrant.LeftTop =>
                    //we are in (pi5/4, pi3/2] or in (pi3/2, pi7/4]
                    ctrl1.x = A.x
                    ctrl1.y = A.y - diff.y / settings.straightenIndex
                    ctrl2.x = B.x
                    ctrl2.y = B.y + diff.y / settings.straightenIndex
            }
        }
        
        drawBezierCurve(context, ctrl1 + correction, ctrl2 + correction, A + correction, B + correction,
            settings.width, color)
    }
    
    private def prepareStraight(context: CanvasRenderingContext2D, color: Color, correction: Vector) {
        
        drawStraightLine(context,
            destinationView.position + correction, originView.position + correction,
            settings.width, color)
    }
    
    def draw(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Vector) {

        drawQuick(context, color, positionCorrection)
    }

    def drawQuick(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Vector) {

        val colorToUse = color.getOrElse(
            if(isSelected) settings.colorSelected else settings.color
        )

        if(1 <= settings.straightenIndex && settings.straightenIndex <= 6) {
            prepareBezierCurve(context, colorToUse, Vector.Zero)
        } else {
            prepareStraight(context, colorToUse, Vector.Zero)
        }
    }

    override def toString: String = {
        "["+originView.toString+"-"+edgeModel.toString+"-"+destinationView.toString+"]"
    }
}
