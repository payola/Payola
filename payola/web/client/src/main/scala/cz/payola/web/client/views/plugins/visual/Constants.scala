package cz.payola.web.client.views.plugins.visual

// TODO maybe consider moving these constants to classes/companion object somehow related to them. I think that
// Color.Black, Color.Red etc. are OK to be placed to the Color companion object. On the other hand COLOR_VERTEX should
// be placed in the Drawer class.
object Constants
{
    val ColorEdge = new Color(150, 150, 150, 0.5)

    val ColorEdgeSelect = new Color(50, 50, 50, 1)

    val ColorVertexHigh = new Color(200, 0, 0, 1)

    val ColorVertexMedium = new Color(0, 180, 0, 0.9)

    val ColorVertexLow = new Color(180, 180, 180, 0.3)

    val ColorVertexDefault = new Color(0, 180, 0, 0.8)

    val ColorText = new Color(200, 200, 200, 1)

    val ColorTextHigh = new Color(50, 50, 50, 1)

    val ColorTextBackground = new Color(255, 255, 255, 0.5)

    val ColorSelectionRect = new Color(150, 150, 150, 0.5)

    val EdgeWidth: Double = 1

    //the higher, the more are edges straight
    val EdgeSIndex = 2

    val VertexSize = new Vector(30, 24)

    //has to be 0 <= x <= Min(VERTEX_HEIGHT, VERTEX_WIDTH)/2 see Drawer.drawVertex(..)
    val VertexCornerRadius: Double = 5

    val SelectLineWidth: Double = 1
}
