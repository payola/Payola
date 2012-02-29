package cz.payola.web.client.views.plugins.visual.graph

import cz.payola.web.client.views.plugins.visual.{Point, Vector}

object LocationDescriptor {

    private val informationPositionCorrection = Vector(0, 4)

    def getVertexInformationPosition(position: Point): Point = {
        position + informationPositionCorrection
    }
    
    def getEdgeInformationPosition(originPosition: Point, destinationPosition: Point): Point = {
        val x = (originPosition.x + destinationPosition.x)/2
        val y = (originPosition.y + destinationPosition.y)/2
        Point(x, y) + informationPositionCorrection
    }
}