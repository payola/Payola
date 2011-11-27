package client

import s2js.adapters.js.browser._
import s2js.adapters.js.dom._

object Index {

    var canvas: Canvas                   = null
    var context: CanvasRenderingContext2D      = null

    def init() {
        canvas = document.createElement("canvas").asInstanceOf[Canvas]
        canvas.width = window.innerWidth
        canvas.height = window.innerHeight
        context = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
        document.getElementById("canvas-holder").appendChild(canvas)
    }
}